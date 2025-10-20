package com.heklast.smartspender.core.di

import com.heklast.smartspender.core.data.remote.ExpenseApi
import com.heklast.smartspender.core.data.remote.firebase.FirebaseExpenseApi

object Services {
    // singletons for now; replace by Koin/Hilt later if you want
    val expenseApi: ExpenseApi by lazy { FirebaseExpenseApi() }
}
