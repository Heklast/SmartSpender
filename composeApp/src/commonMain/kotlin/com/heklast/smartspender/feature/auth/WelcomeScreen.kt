package com.heklast.smartspender.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    authService: AuthService,
    onLoginSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val mint = Color(0xFF3aB09E)
    val mintLight = Color(0xFFDFF7EB)
    val inputBg = Color(0xFFF2F5F4)
    val black = Color(0xFF000000)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(mint)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome!",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = black,
            modifier = Modifier
                .padding(top = 48.dp, bottom = 32.dp)
                .align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
                .background(mintLight, shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                .padding(top = 65.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // Email
            CustomTextField("Username or Email", username, { username = it }, "example@example.com", black, mint, inputBg, KeyboardType.Email)
            // Password
            CustomTextField("Password", password, { password = it }, "••••••••", black, mint, inputBg, KeyboardType.Password, isPassword = true)

            Button(
                onClick = {
                    if (username.isNotBlank() && password.isNotBlank()) {
                        authService.login(username, password) { success, error ->
                            if (success) {
                                onLoginSuccess()
                            } else {
                                onError(error ?: "Unknown error")
                            }
                        }
                    } else {
                        onError("Please fill in both fields")
                    }
                },
                modifier = Modifier
                    .width(170.dp)
                    .height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mint)
            ) {
                Text("Log In", color = black, fontWeight = FontWeight.SemiBold)
            }

            Text(
                text = "Forgot Password?",
                color = mint,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable { onForgotPasswordClick() },
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don’t have an account? ", color = black, fontSize = 14.sp)
                Text(
                    text = "Sign Up",
                    color = mint,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}
