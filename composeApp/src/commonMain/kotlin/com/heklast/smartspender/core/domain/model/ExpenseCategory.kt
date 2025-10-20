package com.heklast.smartspender.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ExpenseCategory {
    FOOD, GROCERIES, TRANSPORT, RENT, HEALTH, ENTERTAINMENT, BILLS, OTHER
}
