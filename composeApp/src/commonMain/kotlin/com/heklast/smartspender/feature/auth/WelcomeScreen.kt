package com.heklast.smartspender.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.foundation.shape.RoundedCornerShape


@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    // State for text fields
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Colors
    val mint = Color(0xFF3aB09E)
    val mintLight = Color(0xFFDFF7EB)      // slightly greenish background for container
    val inputBg = Color(0xFFF2F5F4)        // light mint-gray background for text fields
    val black = Color(0xFF000000)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(mint)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top section — Welcome text
        Text(
            text = "Welcome!",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = black,
            modifier = Modifier
                .padding(top = 48.dp, bottom = 32.dp)
                .align(Alignment.CenterHorizontally)
        )

        // White container below
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
            // Username label + field
            Text(
                text = "Username or Email",
                color = black,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 19.dp)
                    .height(35.dp)
                    .background(inputBg, shape = RoundedCornerShape(14.dp)),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBg,
                    unfocusedContainerColor = inputBg,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // Password label + field
            Text(
                text = "Password",
                color = black,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(bottom = 28.dp)
                    .background(inputBg, shape = RoundedCornerShape(12.dp)),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBg,
                    unfocusedContainerColor = inputBg,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // Login button
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .width(170.dp)
                    .height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mint)
            ) {
                Text(
                    text = "Log In",
                    color = black,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Forgot Password link
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

            // Sign up link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Don’t have an account? ", color = black, fontSize = 14.sp)
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

