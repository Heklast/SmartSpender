package com.heklast.smartspender.core.data

import com.heklast.smartspender.core.domain.model.User
import com.heklast.smartspender.core.data.remote.firebase.FirestoreProvider

object UserRepository {
    private val users get() = FirestoreProvider.db.collection("users")

    suspend fun getUserById(uid: String): User? {
        val snap = users.document(uid).get()
        return try {
            snap.data<User>()
        } catch (_: Throwable) {
            null
        }
    }
}