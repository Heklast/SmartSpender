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
import com.heklast.smartspender.core.auth.AuthRepository
import com.heklast.smartspender.core.data.remote.firebase.FirestoreProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit = {},          // navigate after successful login
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    // State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    // Colors
    val mint = Color(0xFF3aB09E)
    val mintLight = Color(0xFFDFF7EB)
    val inputBg = Color(0xFFF2F5F4)
    val black = Color(0xFF000000)

    val scope = rememberCoroutineScope()

    fun validate(): String? {
        if (email.isBlank() || !email.contains("@")) return "Enter a valid email."
        if (password.isBlank()) return "Password is required."
        return null
    }

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
            Text(
                text = "Email",
                color = black,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .height(48.dp)
                    .background(inputBg, shape = RoundedCornerShape(14.dp)),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBg,
                    unfocusedContainerColor = inputBg,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // Password
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
                    .height(48.dp)
                    .padding(bottom = 8.dp)
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

            // Error text (if any)
            if (errorText != null) {
                Text(
                    text = errorText!!,
                    color = Color(0xFFD32F2F),
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            // Login
            Button(
                enabled = !loading,
                onClick = {
                    errorText = validate()
                    if (errorText != null) return@Button

                    scope.launch {
                        loading = true
                        errorText = null

                        val res = AuthRepository.signIn(email.trim(), password)
                        res.onFailure { t ->
                            loading = false
                            errorText = t.message ?: "Login failed."
                            return@launch
                        }

                        // Ensure /users/{uid} exists and mark ready
                        val uid = Firebase.auth.currentUser?.uid
                        if (uid != null) {
                            runCatching {
                                FirestoreProvider.db
                                    .collection("users")
                                    .document(uid)
                                    .set(mapOf("ready" to true, "email" to email.trim()), merge = true)
                            }
                        }

                        loading = false
                        onLoginClick()
                    }
                },
                modifier = Modifier
                    .width(170.dp)
                    .height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mint)
            ) {
                Text(
                    text = if (loading) "Please wait…" else "Log In",
                    color = black,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Forgot Password
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

            // Sign up
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