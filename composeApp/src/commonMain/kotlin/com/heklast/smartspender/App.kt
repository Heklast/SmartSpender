// shared/src/commonMain/kotlin/com/heklast/smartspender/App.kt
package com.heklast.smartspender

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.heklast.smartspender.core.data.remote.ApiService
import org.jetbrains.compose.ui.tooling.preview.Preview

import com.heklast.smartspender.feature.About.AboutScreen
import com.heklast.smartspender.feature.Profile.ProfileScreen
import com.heklast.smartspender.feature.Statistics.StatisticsScreen
import com.heklast.smartspender.navigation.BottomBar
import org.smartspender.project.feature.home.BeginScreen
import com.heklast.smartspender.feature.intro.IntroScreen
import com.heklast.smartspender.features.auth.ForgotPasswordScreen
import com.heklast.smartspender.features.auth.SignUpScreen
import com.heklast.smartspender.features.auth.WelcomeScreen
import com.heklast.smartspender.navigation.AppState
import com.heklast.smartspender.navigation.Route

import com.heklast.smartspender.core.data.remote.firebase.ensureUserDocCommon
import com.heklast.smartspender.core.data.remote.firebase.signInIfNeeded
import com.heklast.smartspender.feature.expense.add.AddExpenseScreen
import com.heklast.smartspender.feature.expense.list.ExpensesListScreen

@Composable
@Preview
fun App() {
    val appState = remember { AppState() }
    val route by appState.route.collectAsState()
    val listVm = remember { com.heklast.smartspender.feature.expense.list.ExpensesListViewModel() }
    val formVm = remember { com.heklast.smartspender.feature.expense.add.ExpenseFormViewModel() }

    // One-time bootstrap: anonymous sign-in (if needed) + ensure user doc.
    LaunchedEffect(Unit) {
       // signInIfNeeded()        // creates an anonymous user if none
        //ensureUserDocCommon()   // creates/merges /users/{uid} = { ready: true }
        // No Firestore settings calls and no explicit Firestore init here.
    }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                if (route == Route.Profile || route == Route.Statistics || route == Route.About || route == Route.ExpensesList || route == Route.AddExpense ) {
                    BottomBar(
                        current = route,
                        onNavigate = { target -> appState.navigate(target) }
                    )
                }
            }
        ) {
            Surface {
                when (route) {
                    Route.Intro -> IntroScreen(onTimeout = { appState.navigate(Route.Begin) })
                    Route.Begin -> BeginScreen(appState, apiService = ApiService())
                    Route.LogIn -> WelcomeScreen(
                        onLoginClick = { appState.navigate(Route.ExpensesList) },
                        onSignUpClick = { appState.navigate(Route.SignUp) },
                        onForgotPasswordClick = { appState.navigate(Route.ForgotPw) }
                    )
                    Route.SignUp -> SignUpScreen(
                        onLoginClick = { appState.navigate(Route.LogIn) },
                        onSignUpClick = { appState.navigate(Route.Profile) }
                    )
                    Route.ForgotPw -> ForgotPasswordScreen(
                        authService = com.heklast.smartspender.core.di.Services.authService,
                        onConfirmClick = { appState.navigate(Route.LogIn) },
                    )
                    Route.Profile -> ProfileScreen()
                    Route.Statistics -> StatisticsScreen()
                    Route.About -> AboutScreen()
                    Route.ExpensesList -> ExpensesListScreen(
                        vm = listVm,
                        onAddClick = { appState.navigate(Route.AddExpense) }
                    )
                    Route.AddExpense -> AddExpenseScreen(
                        vm = formVm,
                        onSaved = {
                            listVm.refresh()
                            appState.navigate(Route.ExpensesList)
                        },
                        onCancel = { appState.navigate(Route.ExpensesList) }
                    )
                }
            }
        }
    }
}