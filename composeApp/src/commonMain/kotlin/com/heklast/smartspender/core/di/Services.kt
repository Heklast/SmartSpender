package com.heklast.smartspender.core.di

import com.heklast.smartspender.core.data.remote.ExpenseApi
import com.heklast.smartspender.core.data.remote.firebase.FirebaseExpenseApi
import com.heklast.smartspender.core.auth.AuthService
import com.heklast.smartspender.core.auth.FirebaseAuthService

object Services {
    // singletons for now; replace by Koin/Hilt later if you want
    val expenseApi: ExpenseApi by lazy { FirebaseExpenseApi() }
    val authService: AuthService by lazy { FirebaseAuthService() }
}
