package com.heklast.smartspender.core.data.remote.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

/** Anonymous sign-in (safe to call multiple times). */
suspend fun signInIfNeeded() {
    if (Firebase.auth.currentUser == null) {
        Firebase.auth.signInAnonymously()
    }
}

/** Make sure /users/{uid} exists. Safe to call multiple times. */
suspend fun ensureUserDocCommon() {
    val uid = Firebase.auth.currentUser?.uid ?: return
    FirestoreProvider.db.collection("users")
        .document(uid)
        .set(mapOf("ready" to true), merge = true)
}

// ⚠️ No enableOfflineCache() here. Do not set Firestore settings at runtime.