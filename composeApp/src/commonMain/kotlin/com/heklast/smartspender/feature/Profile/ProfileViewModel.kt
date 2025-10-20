package com.heklast.smartspender.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heklast.smartspender.core.data.UserRepository
import com.heklast.smartspender.core.domain.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val testUid: String? = null
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun load() {
       viewModelScope.launch {
           val uid = testUid ?: dev.gitlive.firebase.Firebase.auth.currentUser?.uid?: return@launch
           _user.value = UserRepository.getUserById(uid)
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
}