// shared/src/commonMain/kotlin/com/heklast/smartspender/App.kt
package com.heklast.smartspender

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.heklast.smartspender.feature.About.AboutScreen
import com.heklast.smartspender.feature.Profile.ProfileScreen
import com.heklast.smartspender.feature.Statistics.StatisticsScreen
import com.heklast.smartspender.navigation.BottomBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.smartspender.project.feature.home.BeginScreen
import com.heklast.smartspender.feature.intro.IntroScreen
import com.heklast.smartspender.features.auth.ForgotPasswordScreen
import com.heklast.smartspender.features.auth.SignUpScreen
import com.heklast.smartspender.features.auth.WelcomeScreen
import com.heklast.smartspender.navigation.AppState
import com.heklast.smartspender.navigation.Route

@Composable
@Preview
fun App() {
    val appState = remember { AppState() }
    val route by appState.route.collectAsState()

    MaterialTheme {
        Scaffold(
            bottomBar = {
                if (route == Route.Profile || route == Route.Statistics) {
                    BottomBar(
                        current = route,
                        onNavigate = { target -> appState.navigate(target)}
                    )
                }
            }
        ){
        Surface {
            when (route) {
                Route.Intro -> IntroScreen(
                    onTimeout = { appState.navigate(Route.Begin) }
                )

                Route.Begin -> BeginScreen(appState) // keep passing appState if Begin uses it

                Route.LogIn -> WelcomeScreen(
                    onLoginClick = { appState.navigate(Route.Begin) },          // after login go to Begin (or Home later)
                    onSignUpClick = { appState.navigate(Route.SignUp) },
                    onForgotPasswordClick = { appState.navigate(Route.ForgotPw) }
                )

                Route.SignUp -> SignUpScreen(
                    onLoginClick = { appState.navigate(Route.LogIn) },
                    onSignUpClick = { appState.navigate(Route.Begin) }         // after signup go to Begin
                )

                Route.ForgotPw -> ForgotPasswordScreen(
                    onConfirmClick = { appState.navigate(Route.LogIn) }        // after reset go back to login
                )

                Route.Profile -> ProfileScreen()

                Route.Statistics -> StatisticsScreen()
                Route.About -> AboutScreen()

            }
        }
    }
}}