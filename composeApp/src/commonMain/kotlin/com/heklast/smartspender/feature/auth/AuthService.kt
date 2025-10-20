// composeApp/src/commonMain/kotlin/com/heklast/smartspender/features/auth/AuthService.kt
package com.heklast.smartspender.features.auth

interface AuthService {
    fun signUp(email: String, password: String, onResult: (Boolean, String?) -> Unit)
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit)
    fun resetPassword(email: String, newPassword: String, callback: (Boolean, String?) -> Unit)
}
