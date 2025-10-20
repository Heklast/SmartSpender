package com.heklast.smartspender.core.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val id: String = "",
    val title: String,
    val amount: Double,
    val date: Instant,
    val category: ExpenseCategory,
    val notes: String? = null,
    val tags: List<String> = emptyList()
)
