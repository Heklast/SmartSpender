package com.heklast.smartspender.core.domain.model
import kotlinx.serialization.Serializable

@Serializable
data class Expense (
    val amount: Double=0.0,
    val category: String="",
    val date: String="",
    val notes:String="",
    val title: String="",
)

