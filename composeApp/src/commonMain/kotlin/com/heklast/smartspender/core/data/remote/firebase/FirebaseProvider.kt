package com.heklast.smartspender.core.data.remote.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

/**
 * Single Firestore entry. No runtime settings — avoids "already started" crashes.
 */
object FirestoreProvider {
    val db by lazy { Firebase.firestore }
}