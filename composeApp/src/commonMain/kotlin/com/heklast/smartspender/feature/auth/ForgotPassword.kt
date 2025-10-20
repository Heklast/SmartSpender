package com.heklast.smartspender.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    authService: AuthService,
    onConfirmClick: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Colors
    val mint = Color(0xFF3aB09E)
    val mintLight = Color(0xFFDFF7EB)
    val inputBg = Color(0xFFF5FFF8)
    val black = Color(0xFF000000)

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
            modifier = Modifier
                .padding(top = 48.dp, bottom = 24.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(mintLight, shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Reset Password?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "Enter your email address associated with your account and your new password!",
                fontSize = 14.sp,
                color = black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Email and new password fields
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
            CustomTextField(
                label = "New Password",
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholderText = "••••••••",
                labelColor = black,
                borderColor = mint,
                backgroundColor = inputBg,
                isPassword = true
            )

            // Confirm button
            Button(
                onClick = {
                    if (email.isNotBlank() && newPassword.isNotBlank()) {
                        isLoading = true
                        CoroutineScope(Dispatchers.Main).launch {
                            authService.resetPassword(email, newPassword) { success, error ->
                                isLoading = false
                                if (success) {
                                    onConfirmClick()
                                } else {
                                    onError(error ?: "Failed to reset password")
                                }
                            }
                        }
                    } else {
                        onError("Please fill all fields")
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .width(180.dp)
                    .height(75.dp)
                    .padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mint)
            ) {
                Text(
                    text = if (isLoading) "Please wait..." else "Confirm Password",
                    color = black,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}



