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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

@Composable
fun ProfileScreen(appState: AppState) {
    var enabled by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var uploading by remember { mutableStateOf(false) }

    // New password state & feedback message
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

    // image picker (expect/actual)
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
            contentPadding = PaddingValues(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
        ) {
            item {
                Text(
                    "My Profile",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    color = AppColors.black.copy(alpha = 0.9f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W600,
                    textAlign = TextAlign.Center
                )
            }

            item {
                AsyncImage(
                    model = imageUrl.ifBlank { null },
                    contentDescription = "Profile photo",
                    modifier = Modifier.size(96.dp).clip(CircleShape).background(AppColors.lightGreen)
                )
                Spacer(Modifier.height(12.dp))

                OutlinedButton(onClick = { launchPicker() }, enabled = !uploading) {
                    Text(if (uploading) "Please wait…" else "Change Photo")
                }

                Spacer(Modifier.height(32.dp))
            }

            item {
                Text(
                    "Account Details",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    color = AppColors.black.copy(alpha = 0.9f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500
                )
            }

            item { ProfileTextField("Username", username, { username = it }, enabled) }
            item { ProfileTextField("Phone Number", number, { number = it }, enabled) }
            item { ProfileTextField("Email", email, { email = it }, enabled) }

            item {
                if (enabled) {
                    PasswordField(
                        label = "New Password",
                        value = newPassword,
                        onValueChange = { newPassword = it }
                    )
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(
                        onClick = {
                            onUpdateButtonClick(enabled, { enabled = it }) {
                                scope.launch {
                                    message = null
                                    try {
                                        saveProfileToDb(
                                            uid = uid,
                                            username = username,
                                            number = number,
                                            email = email,
                                            imageUrl = imageUrl
                                        )
                                        if (newPassword.isNotBlank()) {
                                            vm.changePassword(newPassword) { ok, err ->
                                                message = if (ok) {
                                                    newPassword = ""
                                                    "Password updated successfully."
                                                } else {
                                                    err ?: "Failed to update password."
                                                }
                                            }
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
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text(if (enabled) "Save" else "Update Profile")
                    }
                }
                Spacer(Modifier.height(12.dp))
                message?.let {
                    Text(
                        it,
                        color = if (it.contains("fail", true) || it.contains("error", true)) Color.Red else Color(0xFF2E7D32),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // Sign Out
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    OutlinedButton(
                        onClick = {
                            vm.signOut { appState.navigate(Route.Begin) }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Sign Out")
                    }
                }
            }

            // ↓↓↓ Ensure scroll room below content so BottomBar never covers it
            item {
                // Adds exact space for system/bottom bars…
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                // …plus a little extra breathing room
                Spacer(Modifier.height(80.dp))
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
    enabled: Boolean
) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
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
    onValueChange: (String) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
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