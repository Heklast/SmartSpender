package com.heklast.smartspender.core.data

import com.heklast.smartspender.core.domain.model.Expense
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

object ExpensesRepository {
    suspend fun getExpensesForUser(uid: String): List<Expense> {
        val snap = Firebase.firestore
            .collection("users")
            .document(uid)
            .collection("expenses")
            .get()

        return snap.documents.mapNotNull { it.data<Expense>() }
    }
}