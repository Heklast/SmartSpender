// features/auth/ForgotPasswordScreen.kt
package com.heklast.smartspender.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heklast.smartspender.core.auth.AuthService
import com.heklast.smartspender.core.di.Services
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    authService: AuthService = Services.authService,
    onConfirmClick: () -> Unit = {}   // e.g., navigate back to login

) {
    var email by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val mint = Color(0xFF3aB09E)
    val mintLight = Color(0xFFDFF7EB)
    val inputBg = Color(0xFFF5FFF8)
    val black = Color(0xFF000000)

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(mint)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Forgot Password",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = black,
            modifier = Modifier.padding(top = 48.dp, bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(mintLight, shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reset your password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Text(
                text = "Enter your account email. We’ll send you a password reset link.",
                fontSize = 14.sp,
                color = black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Email field
            CustomTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholderText = "example@example.com",
                labelColor = black,
                borderColor = mint,
                backgroundColor = inputBg,
                keyboardType = KeyboardType.Email
            )

            Button(
                enabled = !loading,
                onClick = {
                    if (email.isBlank()) {
                        return@Button
                    }
                    scope.launch {
                        loading = true
                        val res = authService.sendPasswordReset(email.trim())
                        loading = false
                        res.onSuccess { onConfirmClick() }

                    }
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(52.dp)
                    .padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mint)
            ) {
                Text(
                    text = if (loading) "Sending…" else "Send reset email",
                    color = black,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}