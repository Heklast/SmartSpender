package com.heklast.smartspender.feature.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.smartspender.project.core.AppColors
import com.heklast.smartspender.features.profile.presentation.ProfileViewModel
import com.heklast.smartspender.core.data.remote.firebase.FirestoreProvider

@Preview
@Composable
fun ProfileScreen() {
    var enabled by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Get the current UID (nullable)
    val uid: String? = Firebase.auth.currentUser?.uid

    // If no user is signed in, show a friendly state and return early
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

    // Create the ViewModel with the non-null uid
    val vm = remember(uid) { ProfileViewModel(testUid = uid) }
    LaunchedEffect(Unit) { vm.load() }
    val user by vm.user.collectAsState()

    // Reflect current user doc into the editable fields
    LaunchedEffect(user) {
        user?.let {
            username = it.fullName
            number = it.number?.toString() ?: ""
            email = it.email
        }
    }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.mint)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxSize(0.05f))
            Text(
                "My Profile",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp, 2.dp, 2.dp, 100.dp),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.white, shape = RoundedCornerShape(70.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                        Text(
                            user?.fullName.orEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp),
                            color = AppColors.black.copy(alpha = 0.9f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W600,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                        Text(
                            "Account Details",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp, 2.dp, 2.dp, 50.dp),
                            color = AppColors.black.copy(alpha = 0.9f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W500,
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .align(Alignment.CenterHorizontally)
                        ) {

                            ProfileTextField(
                                label = "Username",
                                value = username,
                                onValueChange = { username = it },
                                enabled = enabled
                            )
                            ProfileTextField(
                                label = "Phone Number",
                                value = number,
                                onValueChange = { number = it },
                                enabled = enabled
                            )
                            ProfileTextField(
                                label = "Email",
                                value = email,
                                onValueChange = { email = it },
                                enabled = enabled
                            )

                            Button(
                                onClick = {
                                    onUpdateButtonClick(
                                        enabled = enabled,
                                        setEnabled = { enabled = it },
                                        save = {
                                            scope.launch {
                                                saveProfileToDb(
                                                    uid = uid, // non-null here
                                                    username = username,
                                                    number = number,
                                                    email = email
                                                )
                                                vm.load() // refresh after save
                                            }
                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.mint,
                                    contentColor = AppColors.black
                                ),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) { Text(if (enabled) "Save" else "Update Profile") }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun saveProfileToDb(
    uid: String,
    username: String,
    number: String,
    email: String
) {
    val updates = buildMap<String, Any?> {
        put("fullName", username)
        put("email", email)
        number.toIntOrNull()?.let { put("number", it) } // include only if valid
    }

    // Use FirestoreProvider so settings are already applied once
    FirestoreProvider.db
        .collection("users")
        .document(uid) // <- non-null String
        .set(updates, merge = true)
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean
) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            label,
            color = AppColors.black.copy(alpha = 0.9f),
            fontSize = 15.sp,
            fontWeight = FontWeight.W500
        )
        Spacer(modifier = Modifier.height(10.dp))
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