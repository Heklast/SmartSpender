package com.heklast.smartspender.core.di

import com.heklast.smartspender.core.data.remote.ExpenseApi
import com.heklast.smartspender.core.data.remote.firebase.FirebaseExpenseApi

object Services {
    val expenseApi: ExpenseApi = FirebaseExpenseApi()
}
