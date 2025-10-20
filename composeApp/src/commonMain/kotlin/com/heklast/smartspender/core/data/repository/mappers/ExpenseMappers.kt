package com.heklast.smartspender.core.data.repository.mappers

import com.heklast.smartspender.core.data.remote.dto.CreateExpenseRequest
import com.heklast.smartspender.core.data.remote.dto.ExpenseResponse
import com.heklast.smartspender.core.domain.model.Expense
import com.heklast.smartspender.core.domain.model.ExpenseCategory
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock

fun ExpenseResponse.toDomain(): Expense =
    Expense(
        id = id,
        title = title,
        amount = amount,
        date = Instant.fromEpochMilliseconds(dateEpochMs),
        category = runCatching { ExpenseCategory.valueOf(category) }.getOrElse { ExpenseCategory.OTHER },
        notes = notes,
        tags = tags
    )

// if you use a Draft model, keep this; otherwise you can ignore
data class ExpenseDraft(
    val title: String,
    val amount: Double,
    val dateEpochMs: Long? = null,
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val notes: String? = null,
    val tags: List<String> = emptyList()
)

fun ExpenseDraft.toCreateRequest(): CreateExpenseRequest =
    CreateExpenseRequest(
        title = title,
        amount = amount,
        dateEpochMs = (dateEpochMs ?: Clock.System.now().toEpochMilliseconds()),
        category = category.name,
        notes = notes,
        tags = tags
    )
