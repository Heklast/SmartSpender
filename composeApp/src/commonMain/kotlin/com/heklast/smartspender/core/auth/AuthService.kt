package com.heklast.smartspender.core.auth


interface AuthService {

    suspend fun sendPasswordReset(email: String): Result<Unit>
}
