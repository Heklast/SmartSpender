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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpScreen(
    authService: AuthService,
    onSignUpSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    // States for inputs
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }

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
        // Title
        Text(
            text = "Create Account",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = black,
            modifier = Modifier.padding(top = 48.dp, bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        // Container for input fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(mintLight, shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            CustomTextField("Full Name", fullName, { fullName = it }, placeholderText = "John Doe", black, mint, inputBg)
            CustomTextField("Email", email, { email = it }, placeholderText = "example@example.com", black, mint, inputBg, KeyboardType.Email)
            CustomTextField("Mobile Number", mobile, { mobile = it }, placeholderText = "+123 456", black, mint, inputBg, KeyboardType.Phone)
            CustomTextField("Date of Birth", dob, { dob = it }, placeholderText = "DD/MM/YYYY", black, mint, inputBg)
            CustomTextField("Password", password, { password = it }, placeholderText = "••••••••", black, mint, inputBg, isPassword = true)
            CustomTextField("Confirm Password", confirmPassword, { confirmPassword = it }, placeholderText = "••••••••", black, mint, inputBg, isPassword = true)

            // Terms of use + checkbox
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

            // Sign Up button
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank() && confirmPassword == password && agreedToTerms) {
                        authService.signUp(email, password) { success, error ->
                            if (success) {
                                onSignUpSuccess()
                            } else {
                                onError(error ?: "Unknown error")
                            }
                        }
                    } else {
                        onError("Please fill all fields correctly and accept the terms")
                    }
                },
                modifier = Modifier
                    .width(170.dp)
                    .height(45.dp)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mint)
            ) {
                Text(
                    text = "Sign Up",
                    color = black,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Already have an account?
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
            .height(37.dp)
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
