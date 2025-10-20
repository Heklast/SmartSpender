// features/auth/AuthViewModel.kt
package com.heklast.smartspender.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heklast.smartspender.core.auth.AuthRepository
import com.heklast.smartspender.core.auth.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    val state: StateFlow<AuthState> =
        AuthRepository.authState.stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, AuthState.Loading)

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun signInAnonymously() = scope.launch {
        errorMessage = null
        AuthRepository.signInAnonymously()
            .onFailure { errorMessage = friendly(it) }
    }

    fun signUp(email: String, password: String) = scope.launch {
        errorMessage = null
        AuthRepository.signUp(email, password)
            .onFailure { errorMessage = friendly(it) }
    }

    fun signIn(email: String, password: String) = scope.launch {
        errorMessage = null
        AuthRepository.signIn(email, password)
            .onFailure { errorMessage = friendly(it) }
    }

    fun sendReset(email: String) = scope.launch {
        errorMessage = null
        AuthRepository.sendPasswordReset(email)
            .onFailure { errorMessage = friendly(it) }
    }

    fun signOut() = scope.launch {
        errorMessage = null
        AuthRepository.signOut()
            .onFailure { errorMessage = friendly(it) }
    }

    private fun friendly(t: Throwable) = when {
        t.message?.contains("ADMIN_ONLY", true) == true -> "Sign-up is disabled by the server."
        t.message?.contains("WEAK_PASSWORD", true) == true -> "Password is too weak."
        t.message?.contains("INVALID_EMAIL", true) == true -> "Invalid email address."
        t.message?.contains("USER_NOT_FOUND", true) == true -> "No account with that email."
        t.message?.contains("WRONG_PASSWORD", true) == true -> "Incorrect password."
        else -> t.message ?: "Something went wrong."
    }
}