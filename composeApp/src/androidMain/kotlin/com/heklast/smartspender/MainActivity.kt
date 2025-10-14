package com.heklast.smartspender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.heklast.smartspender.features.auth.WelcomeScreen
import com.heklast.smartspender.features.auth.SignUpScreen
import com.heklast.smartspender.features.auth.ForgotPasswordScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Track which screen is visible
            var currentScreen by remember { mutableStateOf("Welcome") }

            when (currentScreen) {
                "Welcome" -> WelcomeScreen(
                    onLoginClick = { /* TODO: handle login */ },
                    onForgotPasswordClick = { currentScreen = "ForgotPassword" },
                    onSignUpClick = { currentScreen = "SignUp" }
                )
                "SignUp" -> SignUpScreen(
                    onSignUpClick = { /* TODO: handle signup */ },
                    onLoginClick = { currentScreen = "Welcome" }
                )
                "ForgotPassword" -> ForgotPasswordScreen(
                    onConfirmClick = { /* TODO: handle password reset */ }
                )
            }
        }
    }
}
