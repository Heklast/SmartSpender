package com.heklast.smartspender.feature.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import org.smartspender.project.core.AppColors
import com.heklast.smartspender.features.profile.presentation.ProfileViewModel
import com.heklast.smartspender.core.data.remote.firebase.FirestoreProvider
import com.heklast.smartspender.navigation.AppState
import com.heklast.smartspender.navigation.Route
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight

// ✅ Responsive helpers
import com.heklast.smartspender.responsive.rememberWindowSize
import com.heklast.smartspender.responsive.rememberDimens
import com.heklast.smartspender.responsive.WidthClass

@Composable
fun ProfileScreen(appState: AppState) {
    val win = rememberWindowSize()
    val dims = rememberDimens(win)

    val titleSize = when (win.width) {
        WidthClass.Compact -> 22.sp
        WidthClass.Medium -> 26.sp
        WidthClass.Expanded -> 30.sp
    }
    val fieldWidth = when (win.width) {
        WidthClass.Compact -> 0.9f
        WidthClass.Medium -> 0.7f
        WidthClass.Expanded -> 0.6f
    }

    var enabled by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var uploading by remember { mutableStateOf(false) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    val uid = Firebase.auth.currentUser?.uid

    if (uid == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(AppColors.mint),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Please sign in to view your profile.",
                color = AppColors.black,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val vm = remember(uid) { ProfileViewModel(testUid = uid) }
    LaunchedEffect(Unit) { vm.load() }
    val user by vm.user.collectAsState()

    LaunchedEffect(user) {
        user?.let {
            username = it.fullName
            number = it.number?.toString() ?: ""
            email = it.email
            imageUrl = it.imageURL
        }
    }

    val launchPicker = rememberImagePicker { uriString -> imageUrl = uriString }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize().background(AppColors.mint)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.white, RoundedCornerShape(topStart = 70.dp, topEnd = 70.dp))
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = dims.gap * 6,
                start = dims.padding,
                end = dims.padding,
                bottom = dims.gap * 8
            )
        ) {
            item {
                Text(
                    "My Profile",
                    modifier = Modifier.fillMaxWidth().padding(bottom = dims.gap * 3),
                    color = AppColors.black.copy(alpha = 0.9f),
                    fontSize = titleSize,
                    fontWeight = FontWeight.W600,
                    textAlign = TextAlign.Center
                )
            }

            item {
                AsyncImage(
                    model = imageUrl.ifBlank { null },
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .size(
                            when (win.width) {
                                WidthClass.Compact -> 96.dp
                                WidthClass.Medium -> 120.dp
                                WidthClass.Expanded -> 140.dp
                            }
                        )
                        .clip(CircleShape)
                        .background(AppColors.lightGreen)
                )
                Spacer(Modifier.height(dims.gap * 2))

                OutlinedButton(onClick = { launchPicker() }, enabled = !uploading) {
                    Text(if (uploading) "Please wait…" else "Change Photo")
                }

                Spacer(Modifier.height(dims.gap * 4))
            }

            item {
                Text(
                    "Account Details",
                    modifier = Modifier.fillMaxWidth().padding(bottom = dims.gap * 2),
                    color = AppColors.black.copy(alpha = 0.9f),
                    fontSize = (titleSize.value - 2).sp,
                    fontWeight = FontWeight.W500
                )
            }

            item { ProfileTextField("Username", username, { username = it }, enabled, fieldWidth) }
            item { ProfileTextField("Phone Number", number, { number = it }, enabled, fieldWidth) }
            item { ProfileTextField("Email", email, { email = it }, enabled, fieldWidth) }

            item {
                if (enabled) {
                    val hasPasswordProvider = Firebase.auth.currentUser
                        ?.providerData?.any { it.providerId == "password" } == true

                    if (hasPasswordProvider) {
                        PasswordField(
                            label = "Current Password",
                            value = currentPassword,
                            fieldWidth= fieldWidth,
                            onValueChange = { currentPassword = it }
                        )
                    }

                    PasswordField(
                        label = if (hasPasswordProvider) "New Password" else "Set Password",
                        value = newPassword,
                        fieldWidth= fieldWidth,
                        onValueChange = { newPassword = it }
                    )
                }
            }

            item {
                Spacer(Modifier.height(dims.gap * 3))
                Button(
                    onClick = {
                        onUpdateButtonClick(enabled, { enabled = it }) {
                            scope.launch {
                                message = null

                                val userNow = Firebase.auth.currentUser
                                if (userNow == null) {
                                    message = "You’re signed out."
                                    return@launch
                                }

                                try {
                                    // Save Firestore profile
                                    saveProfileToDb(uid, username, number, email, imageUrl)

                                    if (newPassword.isNotBlank()) {
                                        val hasPw = userNow.providerData.any { it.providerId == "password" }
                                        val r = vm.updatePasswordOrLink(
                                            email = email.trim(),
                                            currentPassword = if (hasPw) currentPassword else null,
                                            newPassword = newPassword
                                        )
                                        message = r.fold(
                                            onSuccess = {
                                                currentPassword = ""
                                                newPassword = ""
                                                "Password updated successfully."
                                            },
                                            onFailure = { err ->
                                                when {
                                                    err.message?.contains("requires-recent-login", true) == true ->
                                                        "Please log in again and retry changing your password."
                                                    err.message?.contains("administrator", true) == true ->
                                                        "This account can’t change password here. Try linking email/password first."
                                                    else -> err.message ?: "Failed to update password."
                                                }
                                            }
                                        )
                                    } else {
                                        message = "Profile updated."
                                    }

                                    vm.load()
                                } catch (t: Throwable) {
                                    message = t.message ?: "Update failed."
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.mint,
                        contentColor = AppColors.black
                    ),
                    modifier = Modifier.fillMaxWidth(fieldWidth)
                ) {
                    Text(if (enabled) "Save" else "Update Profile")
                }

                message?.let {
                    Spacer(Modifier.height(dims.gap))
                    Text(
                        it,
                        color = if (it.contains("fail", true) || it.contains("error", true))
                            Color.Red else Color(0xFF2E7D32),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(Modifier.height(dims.gap * 2))
            }

            item {
                OutlinedButton(
                    onClick = { vm.signOut { appState.navigate(Route.Begin) } },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier.fillMaxWidth(fieldWidth)
                ) {
                    Text("Sign Out")
                }

                Spacer(Modifier.height(dims.gap * 8))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

private suspend fun saveProfileToDb(
    uid: String,
    username: String,
    number: String,
    email: String,
    imageUrl: String
) {
    val updates = buildMap<String, Any?> {
        put("fullName", username)
        put("email", email)
        number.toIntOrNull()?.let { put("number", it) }
        put("imageURL", imageUrl)
    }
    FirestoreProvider.db.collection("users").document(uid).set(updates, merge = true)
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    fieldWidth: Float
) {
    Column(modifier = Modifier.fillMaxWidth(fieldWidth).padding(bottom = 20.dp)) {
        Text(label, color = AppColors.black.copy(alpha = 0.9f), fontSize = 15.sp, fontWeight = FontWeight.W500)
        Spacer(Modifier.height(10.dp))
        TextField(
            value = value,
            enabled = enabled,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = AppColors.lightGreen,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = AppColors.lightGreen,
            ),
            shape = RoundedCornerShape(20.dp),
        )
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fieldWidth: Float
) {
    var visible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth(fieldWidth).padding(bottom = 20.dp)) {
        Text(label, color = AppColors.black.copy(alpha = 0.9f), fontSize = 15.sp, fontWeight = FontWeight.W500)
        Spacer(Modifier.height(10.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { visible = !visible }) { Text(if (visible) "Hide" else "Show") }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = AppColors.lightGreen,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = AppColors.lightGreen,
            ),
            shape = RoundedCornerShape(20.dp),
        )
    }
}

private fun onUpdateButtonClick(
    enabled: Boolean,
    setEnabled: (Boolean) -> Unit,
    save: () -> Unit
) {
    if (!enabled) setEnabled(true) else {
        save()
        setEnabled(false)
    }
}

@Composable
expect fun rememberImagePicker(onPicked: (String) -> Unit): () -> Unit