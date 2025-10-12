package org.smartspender.project.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.smartspender.project.core.AppColors
import smartspender.composeapp.generated.resources.Res
import smartspender.composeapp.generated.resources.darkgreen_logo
import smartspender.composeapp.generated.resources.light

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.lightGreen)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.darkgreen_logo),
                contentDescription = "SmartSpender logo",
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SmartSpender",
                color = AppColors.darkGreen,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Get to know your habits.",
                modifier = Modifier
                    .fillMaxWidth().padding(4.dp),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = 15.sp,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Let's start our Smart Journey together!",
                modifier = Modifier
                    .fillMaxWidth().padding(4.dp),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.mint,
                    contentColor = AppColors.black
                ),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
            ) {
                Text("Sign Up")
            }
        }

        Column(modifier =Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth().padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally){
        Text(
            text = "Already have an account?",
            color = AppColors.black.copy(alpha = 0.9f),
            fontSize = 10.sp,
        )
            Button(
                onClick = { /* handle sign up */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.mint,
                    contentColor = AppColors.black
                ),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
            ){Text("Log In")}

    }}

}