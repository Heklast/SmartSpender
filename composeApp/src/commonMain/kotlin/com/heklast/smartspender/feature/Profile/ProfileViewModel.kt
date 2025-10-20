package com.heklast.smartspender.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heklast.smartspender.core.data.UserRepository
import com.heklast.smartspender.core.domain.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class ProfileViewModel(
    private val testUid: String? = null
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun load() {
        viewModelScope.launch {
            val uid = testUid ?: Firebase.auth.currentUser?.uid ?: return@launch

            // 1) show whatever is cached (works offline immediately)
            val cached = UserRepository.getUserById(uid, preferCache = true)
            if (cached != null) _user.value = cached

            // 2) then try to refresh from server; keep cached if it fails (offline)
            val fresh = UserRepository.getUserById(uid, preferCache = false)
            if (fresh != null) _user.value = fresh
        }
    }

    fun changePassword(
        newPassword: String,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val u = Firebase.auth.currentUser
                if (u == null) {
                    onResult(false, "No user is signed in.")
                    return@launch
                }
                if (newPassword.isBlank()) {
                    onResult(false, "Password cannot be empty.")
                    return@launch
                }
                u.updatePassword(newPassword)
                onResult(true, null)
            } catch (t: Throwable) {
                onResult(false, t.message ?: "Failed to change password.")
            }
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            Firebase.auth.signOut() // suspend
            onSignedOut()
        }
    }
    suspend fun updatePasswordOrLink(
        email: String,
        currentPassword: String?, // null if linking for the first time
        newPassword: String
    ): Result<Unit> = withContext(Dispatchers.Default) {
        val user = Firebase.auth.currentUser ?: return@withContext Result.failure(
            IllegalStateException("Not signed in")
        )

        try {
            val hasPasswordProvider = user.providerData.any { it.providerId == "password" }

            if (hasPasswordProvider) {
                // must reauthenticate before updating password
                if (currentPassword.isNullOrBlank()) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Enter your current password to change it.")
                    )
                }
                val cred = EmailAuthProvider.credential(email, currentPassword)
                user.reauthenticate(cred)
                user.updatePassword(newPassword)
            } else {
                // first time adding a password (anonymous or social login)
                val cred = EmailAuthProvider.credential(email, newPassword)
                user.linkWithCredential(cred)
            }

            Result.success(Unit)
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}