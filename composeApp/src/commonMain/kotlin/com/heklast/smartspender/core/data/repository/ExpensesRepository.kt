package com.heklast.smartspender.core.data

import com.heklast.smartspender.core.domain.model.Expense
import com.heklast.smartspender.core.data.remote.firebase.FirestoreProvider

object ExpensesRepository {
    suspend fun getExpensesForUser(uid: String): List<Expense> {
        val snap = FirestoreProvider.db
            .collection("users")
            .document(uid)
            .collection("expenses")
            .get()

        return snap.documents.mapNotNull { it.data<Expense>() }
    }
}