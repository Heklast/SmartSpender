package com.heklast.smartspender.core.data

import com.heklast.smartspender.core.data.remote.firebase.FirestoreProvider
import com.heklast.smartspender.core.domain.model.User
import dev.gitlive.firebase.firestore.Source

object UserRepository {
    private val users get() = FirestoreProvider.db.collection("users")

    /** When preferCache=true: try CACHE → then SERVER; otherwise SERVER → then CACHE. */
    suspend fun getUserById(uid: String, preferCache: Boolean): User? =
        if (preferCache) getFrom(Source.CACHE, uid) ?: getFrom(Source.DEFAULT, uid)
        else            getFrom(Source.DEFAULT, uid) ?: getFrom(Source.CACHE, uid)

    private suspend fun getFrom(src: Source, uid: String): User? = try {
        users.document(uid).get(src).data<User>()
    } catch (_: Throwable) { null }
}