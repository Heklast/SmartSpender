package com.heklast.smartspender

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.heklast.smartspender.feature.Profile.ProfileScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.smartspender.project.feature.home.BeginScreen

import com.heklast.smartspender.feature.intro.IntroScreen
import com.heklast.smartspender.navigation.AppState
import com.heklast.smartspender.navigation.Route

@Composable
@Preview
fun App() {
    val appState = remember { AppState() }
    val route by appState.route.collectAsState()

    MaterialTheme {
        Surface {
    when (route) {
        Route.Intro -> IntroScreen(onTimeout = { appState.navigate(Route.Begin) })
        Route.Begin -> BeginScreen(appState)
        Route.Profile -> ProfileScreen()
       // Route.Signup -> SignupScreen()
        //Route.ForgotPw -> ForgotPwScreen()
        //Route.Home -> HomeScreen()
        //Route.AddExpense -> AddExpenseScreen()
       // Route.Statistics -> StatisticsScreen()

    }        }
    }
}
