// core/auth/AuthRepository.kt
package com.heklast.smartspender.core.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

sealed interface AuthState {
    data object Loading : AuthState
    data class SignedOut(val reason: String? = null) : AuthState
    data class SignedIn(val uid: String, val isAnonymous: Boolean) : AuthState
}

object AuthRepository {

    /** Observe auth state as a Flow that your UI can collect. */
    val authState: Flow<AuthState> = authUserFlow()
        .map { user ->
            when (user) {
                null -> AuthState.SignedOut()
                else -> AuthState.SignedIn(uid = user.uid, isAnonymous = user.isAnonymous)
            }
        }

    private fun authUserFlow(): Flow<FirebaseUser?> = flow {
        var lastUid: String? = null
        while (true) {
            val current = Firebase.auth.currentUser
            if (current?.uid != lastUid) {
                emit(current)
                lastUid = current?.uid
            }
            delay(1000) // check every second (or longer if you prefer)
        }
    }

    /** Anonymous sign-in (guest). Safe to call multiple times. */
    suspend fun signInAnonymously(): Result<Unit> = runCatching {
        if (Firebase.auth.currentUser == null) Firebase.auth.signInAnonymously()
    }

    /** Email/password sign-up. If you’re currently anonymous, this will UPGRADE the anonymous user (UID stays the same). */
    suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        val current = Firebase.auth.currentUser
        if (current?.isAnonymous == true) {
            // upgrade guest → email/password (keeps UID)
            val cred = EmailAuthProvider.credential(email, password)
            current.linkWithCredential(cred)
        } else {
            Firebase.auth.createUserWithEmailAndPassword(email, password)
        }
    }

    /** Sign-in with email/password. */
    suspend fun signIn(email: String, password: String): Result<Unit> =
        runCatching { Firebase.auth.signInWithEmailAndPassword(email, password) }

    /** Send password reset email. */
    suspend fun sendPasswordReset(email: String): Result<Unit> =
        runCatching { Firebase.auth.sendPasswordResetEmail(email) }

    /** Sign out. */
    suspend fun signOut(): Result<Unit> = runCatching { Firebase.auth.signOut() }
}