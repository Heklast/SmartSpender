package com.heklast.smartspender.core.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class FirebaseAuthService : AuthService {
    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        runCatching { Firebase.auth.sendPasswordResetEmail(email) }
}