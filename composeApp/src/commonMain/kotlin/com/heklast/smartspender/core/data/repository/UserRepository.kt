package com.heklast.smartspender.core.data

import com.heklast.smartspender.core.domain.model.User
import com.heklast.smartspender.core.data.remote.firebase.FirestoreProvider
import dev.gitlive.firebase.firestore.Source

object UserRepository {
    private val users get() = FirestoreProvider.db.collection("users")

    /**
     * Reads user by id. When offline (or server fails), we retry from the local cache.
     * Returns null if neither server nor cache have a document.
     */
    suspend fun getUserById(uid: String): User? {
        val doc = users.document(uid)
        // 1) Try normal fetch (DEFAULT -> hits server when possible)
        val snap = try {
            doc.get()
        } catch (_: Throwable) {
            // 2) Fallback to local cache (works only if the doc was seen before while online)
            try { doc.get(Source.CACHE) } catch (_: Throwable) { null }
        } ?: return null

        return try {
            snap.data<User>()
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * Upsert user profile. Works offline too — Firestore queues the write and syncs later.
     * Keep it handy if your team edits profile fields.
     */

    suspend fun upsertUser(uid: String, user: User){
        users.document(uid).set(user, merge = true)
    }
}