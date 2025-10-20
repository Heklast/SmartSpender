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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
fun SignUpScreen(
    onSignUpClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    // Inputs
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }

    // UI state
    var loading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    // Colors
    val mint = Color(0xFF3aB09E)
    val mintLight = Color(0xFFDFF7EB)
    val inputBg = Color(0xFFF5FFF8)
    val black = Color(0xFF000000)

    val scope = rememberCoroutineScope()

    // Responsive tokens
    val win = rememberWindowSize()
    val dims = rememberDimens(win)
    val isWide = win.width >= WidthClass.Medium
    val titleSize = if (isWide) 34.sp else 32.sp
    val labelSize = if (isWide) 16.sp else 14.sp
    val bodySize  = if (isWide) 15.sp else 14.sp

    fun validate(): String? {
        if (!agreedToTerms) return "Please agree to the Terms to continue."
        if (fullName.isBlank()) return "Full name is required."
        if (email.isBlank() || !email.contains("@")) return "Enter a valid email."
        if (password.length < 6) return "Password must be at least 6 characters."
        if (password != confirmPassword) return "Passwords do not match."
        return null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mint)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
    ) {
        // Center the card on tablets for nicer readability
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
                        text = "Create Account",
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold,
                        color = black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, bottom = dims.gap * 2),
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
                            modifier = Modifier.padding(
                                start = dims.padding,
                                end = dims.padding,
                                top = dims.padding,
                                bottom = dims.padding
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomTextField(
                                label = "Full Name",
                                value = fullName,
                                onValueChange = { fullName = it },
                                placeholderText = "John Doe",
                                labelColor = black,
                                borderColor = mint,
                                backgroundColor = inputBg,
                                labelFontSize = labelSize
                            )
                            CustomTextField(
                                label = "Email",
                                value = email,
                                onValueChange = { email = it },
                                placeholderText = "example@example.com",
                                labelColor = black,
                                borderColor = mint,
                                backgroundColor = inputBg,
                                keyboardType = KeyboardType.Email,
                                labelFontSize = labelSize
                            )
                            CustomTextField(
                                label = "Mobile Number",
                                value = mobile,
                                onValueChange = { mobile = it },
                                placeholderText = "+123 456",
                                labelColor = black,
                                borderColor = mint,
                                backgroundColor = inputBg,
                                keyboardType = KeyboardType.Phone,
                                labelFontSize = labelSize
                            )
                            CustomTextField(
                                label = "Date of Birth",
                                value = dob,
                                onValueChange = { dob = it },
                                placeholderText = "DD/MM/YYYY",
                                labelColor = black,
                                borderColor = mint,
                                backgroundColor = inputBg,
                                labelFontSize = labelSize
                            )

                            CustomTextField(
                                label = "Password",
                                value = password,
                                onValueChange = { password = it },
                                placeholderText = "••••••••",
                                labelColor = black,
                                borderColor = mint,
                                backgroundColor = inputBg,
                                keyboardType = KeyboardType.Password,
                                isPassword = !showPassword,
                                labelFontSize = labelSize
                            )

                            CustomTextField(
                                label = "Confirm Password",
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                placeholderText = "••••••••",
                                labelColor = black,
                                borderColor = mint,
                                backgroundColor = inputBg,
                                keyboardType = KeyboardType.Password,
                                isPassword = !showConfirmPassword,
                                labelFontSize = labelSize
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = agreedToTerms,
                                    onCheckedChange = { agreedToTerms = it },
                                    colors = CheckboxDefaults.colors(checkedColor = mint)
                                )
                                Text(
                                    text = "By continuing, you agree to Terms of Use and Privacy Policy.",
                                    color = black,
                                    fontSize = bodySize,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            if (errorText != null) {
                                Text(
                                    text = errorText!!,
                                    color = Color(0xFFD32F2F),
                                    fontSize = bodySize,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
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
                                        val authRes = AuthRepository.signUp(email.trim(), password)
                                        authRes.onFailure { t ->
                                            loading = false
                                            errorText = t.message ?: "Sign up failed."
                                            return@launch
                                        }

                                        val uid = Firebase.auth.currentUser?.uid
                                        if (uid == null) {
                                            loading = false
                                            errorText = "User not signed in after sign up."
                                            return@launch
                                        }

                                        val updates = buildMap<String, Any?> {
                                            put("fullName", fullName.trim())
                                            put("email", email.trim())
                                            mobile.toLongOrNull()?.let { put("number", it) }
                                            if (dob.isNotBlank()) put("dob", dob.trim())
                                            put("ready", true)
                                        }

                                        runCatching {
                                            FirestoreProvider.db
                                                .collection("users")
                                                .document(uid)
                                                .set(updates, merge = true)
                                        }.onFailure { t ->
                                            loading = false
                                            errorText = t.message ?: "Failed to save profile."
                                            return@launch
                                        }

                                        loading = false
                                        onSignUpClick()
                                    }
                                },
                                modifier = Modifier
                                    .width(if (isWide) 220.dp else 170.dp)
                                    .height(48.dp)
                                    .padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = mint)
                            ) {
                                Text(
                                    text = if (loading) "Please wait…" else "Sign Up",
                                    color = black,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = if (isWide) 16.sp else 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(dims.gap * 3))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Already have an account? ",
                                    color = black,
                                    fontSize = bodySize
                                )
                                Text(
                                    text = "Log In",
                                    color = mint,
                                    fontSize = bodySize,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier.clickable { onLoginClick() }
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

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String = "",
    labelColor: Color,
    borderColor: Color,
    backgroundColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    labelFontSize: androidx.compose.ui.unit.TextUnit = 14.sp
) {
    Text(
        text = label,
        color = labelColor,
        fontSize = labelFontSize,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        textAlign = TextAlign.Start
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .height(48.dp)
            .background(backgroundColor, shape = RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        placeholder = {
            Text(
                text = placeholderText,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        },
        textStyle = LocalTextStyle.current.copy(lineHeight = 18.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}