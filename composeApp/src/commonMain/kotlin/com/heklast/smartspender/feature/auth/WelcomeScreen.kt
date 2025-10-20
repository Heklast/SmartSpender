package com.heklast.smartspender.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heklast.smartspender.core.auth.AuthRepository
import com.heklast.smartspender.core.data.remote.firebase.FirestoreProvider
import com.heklast.smartspender.responsive.WidthClass
import com.heklast.smartspender.responsive.rememberDimens
import com.heklast.smartspender.responsive.rememberWindowSize
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
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

    // Responsive tokens
    val win = rememberWindowSize()
    val dims = rememberDimens(win)
    val isWide = win.width >= WidthClass.Medium
    val titleSize = if (isWide) 38.sp else 36.sp
    val labelSize = if (isWide) 16.sp else 14.sp
    val bodySize  = if (isWide) 15.sp else 14.sp

    fun validate(): String? {
        if (email.isBlank() || !email.contains("@")) return "Enter a valid email."
        if (password.isBlank()) return "Password is required."
        return null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mint)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isWide) dims.padding else 0.dp),
            contentAlignment = if (isWide) Alignment.TopCenter else Alignment.TopStart
        ) {
            val maxContentWidth = if (isWide) 720.dp else Dp.Unspecified

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (maxContentWidth != Dp.Unspecified) Modifier.width(maxContentWidth) else Modifier)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = dims.padding, vertical = dims.padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = dims.padding * 2)
            ) {
                item {
                    Text(
                        text = "Welcome!",
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold,
                        color = black,
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = dims.gap * 3)
                            .align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    Surface(
                        color = mintLight,
                        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp, start = dims.padding, end = dims.padding, bottom = dims.padding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(dims.gap * 1.5f)
                        ) {
                            Text(
                                text = "Email",
                                color = black,
                                fontSize = labelSize,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 60.dp)      // was .height(48.dp)
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

                            Text(
                                text = "Password",
                                color = black,
                                fontSize = labelSize,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 60.dp)      // was .height(48.dp)
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

                            if (errorText != null) {
                                Text(
                                    text = errorText!!,
                                    color = Color(0xFFD32F2F),
                                    fontSize = bodySize,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp),
                                    textAlign = TextAlign.Start
                                )
                            }

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

                                        val uid = Firebase.auth.currentUser?.uid
                                        if (uid != null) {
                                            runCatching {
                                                FirestoreProvider.db
                                                    .collection("users")
                                                    .document(uid)
                                                    .set(
                                                        mapOf("ready" to true, "email" to email.trim()),
                                                        merge = true
                                                    )
                                            }
                                        }

                                        loading = false
                                        onLoginClick()
                                    }
                                },
                                modifier = Modifier
                                    .width(if (isWide) 220.dp else 170.dp)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = mint)
                            ) {
                                Text(
                                    text = if (loading) "Please wait…" else "Log In",
                                    color = black,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = if (isWide) 16.sp else 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(dims.gap * 2))

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Don’t have an account? ", color = black, fontSize = bodySize)
                                Text(
                                    text = "Sign Up",
                                    color = mint,
                                    fontSize = bodySize,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { onSignUpClick() }
                                )
                            }
                        }
                    }
                }

                // breathing room above bottom bar
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}