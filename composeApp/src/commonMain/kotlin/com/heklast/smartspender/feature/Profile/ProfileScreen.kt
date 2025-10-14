package com.heklast.smartspender.feature.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.smartspender.project.core.AppColors

@Preview
@Composable
fun ProfileScreen( //onSaveToDb: suspend (username: String, phone: String, email: String) -> Unit
){

    var enabled by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var number    by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.mint)
    ) {
        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.fillMaxSize(0.05f))
            Text("My Profile", modifier = Modifier
            .fillMaxWidth().padding(2.dp,2.dp,2.dp,100.dp),
            color = AppColors.black.copy(alpha = 0.9f),
            fontSize = 20.sp,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Center)

        Box(modifier=Modifier.fillMaxSize().background(AppColors.white, shape = RoundedCornerShape(70.dp))){
            Column(modifier= Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
                Text("Hekla",modifier = Modifier
                    .fillMaxWidth().padding(2.dp),
                    color = AppColors.black.copy(alpha = 0.9f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    textAlign = TextAlign.Center )
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier=Modifier.fillMaxSize().padding(10.dp)){
                    Text("Account Details", modifier = Modifier
                        .fillMaxWidth().padding(2.dp, 2.dp,2.dp, 50.dp),
                        color = AppColors.black.copy(alpha = 0.9f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                        )

                    Column(modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.CenterHorizontally)){

                        ProfileTextField("Username", username, onValueChange = { username = it }, enabled)
                        ProfileTextField("Phone Number",number, onValueChange = { number = it }, enabled)
                        ProfileTextField("Email",email, onValueChange = { email = it }, enabled)

                            Button(   onClick = {  onUpdateButtonClick(
                                enabled = enabled,
                                setEnabled = { enabled = it },
                                save = { scope.launch { //onSaveToDb(username, number, email)
                                     } }
                            ) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.mint,
                                    contentColor = AppColors.black
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            ){ Text(if (enabled) "Save" else "Update Profile")}


                    }

                }

            }}
        }
    }
    }

@Composable
fun ProfileTextField(label: String, value: String,
                     onValueChange: (String) -> Unit, enabled: Boolean){
    Column(modifier = Modifier.padding(0.dp,0.dp,0.dp,20.dp)) {
        Text(label,color = AppColors.black.copy(alpha = 0.9f),
            fontSize = 15.sp,
            fontWeight = FontWeight.W500)
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = value, enabled=enabled, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = AppColors.lightGreen,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor=Color.Transparent,
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
    if (!enabled) {
        setEnabled(true)
    } else {
        save()
        setEnabled(false)
    }
}
