package com.heklast.smartspender.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heklast.smartspender.core.auth.AuthRepository
import com.heklast.smartspender.core.data.remote.firebase.FirestoreProvider
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

    fun validate(): String? {
        if (!agreedToTerms) return "Please agree to the Terms to continue."
        if (fullName.isBlank()) return "Full name is required."
        if (email.isBlank() || !email.contains("@")) return "Enter a valid email."
        if (password.length < 6) return "Password must be at least 6 characters."
        if (password != confirmPassword) return "Passwords do not match."
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
            text = "Create Account",
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
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            CustomTextField("Full Name", fullName, { fullName = it }, placeholderText = "John Doe", labelColor = black, borderColor = mint, backgroundColor = inputBg)
            CustomTextField("Email", email, { email = it }, placeholderText = "example@example.com", labelColor = black, borderColor = mint, backgroundColor = inputBg, keyboardType = KeyboardType.Email)
            CustomTextField("Mobile Number", mobile, { mobile = it }, placeholderText = "+123 456", labelColor = black, borderColor = mint, backgroundColor = inputBg, keyboardType = KeyboardType.Phone)
            CustomTextField("Date of Birth", dob, { dob = it }, placeholderText = "DD/MM/YYYY", labelColor = black, borderColor = mint, backgroundColor = inputBg)

            // Password
            CustomTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholderText = "••••••••",
                labelColor = black, borderColor = mint, backgroundColor = inputBg,
                keyboardType = KeyboardType.Password,
                isPassword = !showPassword
            )

            // Confirm password
            CustomTextField(
                label = "Confirm Password",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholderText = "••••••••",
                labelColor = black, borderColor = mint, backgroundColor = inputBg,
                keyboardType = KeyboardType.Password,
                isPassword = !showConfirmPassword
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
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Error message
            if (errorText != null) {
                Text(
                    text = errorText!!,
                    color = Color(0xFFD32F2F),
                    fontSize = 13.sp,
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
                        // 1) Create account or upgrade anonymous → email/password
                        val authRes = AuthRepository.signUp(email.trim(), password)
                        authRes.onFailure { t ->
                            loading = false
                            errorText = t.message ?: "Sign up failed."
                            return@launch
                        }

                        // 2) Save profile fields into /users/{uid}
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
                        onSignUpClick() // navigate onward (e.g., to Begin/Home)
                    }
                },
                modifier = Modifier
                    .width(170.dp)
                    .height(45.dp)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mint)
            ) {
                Text(
                    text = if (loading) "Please wait…" else "Sign Up",
                    color = black,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = black,
                    fontSize = 14.sp
                )
                Text(
                    text = "Log In",
                    color = mint,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onLoginClick() }
                )
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
    isPassword: Boolean = false
) {
    Text(
        text = label,
        color = labelColor,
        fontSize = 14.sp,
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
            .height(44.dp)
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
        textStyle = LocalTextStyle.current.copy(lineHeight = 14.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}