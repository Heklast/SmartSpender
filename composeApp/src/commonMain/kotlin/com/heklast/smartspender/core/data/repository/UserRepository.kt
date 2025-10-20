package com.heklast.smartspender.core.data

import com.heklast.smartspender.core.domain.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

object UserRepository {
    private val users = Firebase.firestore.collection("users")

    suspend fun getUserById(uid: String): User? {
        val snap = users.document(uid).get()
        return try {
            snap.data<User>()
        } catch (_: Throwable) {
            null
        }
    }

}