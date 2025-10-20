package com.heklast.smartspender.core.data.remote.firebase

import com.heklast.smartspender.core.common.Result
import com.heklast.smartspender.core.data.remote.UserApi
import com.heklast.smartspender.core.data.remote.UserProfile
import dev.gitlive.firebase.firestore.Source

import kotlinx.coroutines.flow.map

class FirebaseUserApi : UserApi {
    private val col get() = FirestoreProvider.db.collection("users")

    override suspend fun get(uid: String): Result<UserProfile> = try {
        val snap = col.document(uid).get(source = Source.DEFAULT) // cache first
        Result.Ok(snap.data<UserProfile>().copy(uid = uid))
    } catch (t: Throwable) {
        Result.Err(t)
    }

    override fun watch(uid: String) =
        col.document(uid).snapshots.map { snap ->
            try {
                Result.Ok(snap.data<UserProfile>().copy(uid = uid))
            } catch (t: Throwable) {
                Result.Err(t)
            }
        }

    override suspend fun update(uid: String, profile: UserProfile): Result<Unit> = try {
        col.document(uid).set(profile) // optimistic local write, syncs later
        Result.Ok(Unit)
    } catch (t: Throwable) {
        Result.Err(t)
    }
}