package com.heklast.smartspender.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateExpenseRequest(
    val title: String,
    val amount: Double,          // decimal (2 dp)
    val dateEpochMs: Long,       // Instant in epoch millis
    val category: String,        // e.g., "FOOD"
    val notes: String? = null,
    val tags: List<String>? = null
)

@Serializable
data class ExpenseResponse(
    val id: String,
    val title: String,
    val amount: Double,
    val dateEpochMs: Long,
    val category: String,
    val notes: String? = null,
    val tags: List<String> = emptyList()
)

@Serializable
data class ListExpensesResponse(
    val items: List<ExpenseResponse>,
    val total: Int? = null
)
