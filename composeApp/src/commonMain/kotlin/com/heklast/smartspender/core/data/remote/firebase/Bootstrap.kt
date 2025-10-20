package com.heklast.smartspender.core.data.remote.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.firestoreSettings

/** Anonymous sign-in (safe to call multiple times). */
suspend fun signInIfNeeded() {
    if (Firebase.auth.currentUser == null) {
        Firebase.auth.signInAnonymously()
    }
}

/** Make sure /users/{uid} exists. Safe to call multiple times. */
suspend fun ensureUserDocCommon() {
    val uid = Firebase.auth.currentUser?.uid ?: return
    Firebase.firestore.collection("users")
        .document(uid)
        .set(mapOf("ready" to true), merge = true)
}

/**
 * Keep it simple: apply default Firestore settings which include
 * persistence on Android; on iOS this is still fine (no-op-ish).
 * You can refine later if you want custom cache sizes.
 */
suspend fun enableOfflineCache() {
    Firebase.firestore.settings = firestoreSettings { /* defaults are fine */ }
}
