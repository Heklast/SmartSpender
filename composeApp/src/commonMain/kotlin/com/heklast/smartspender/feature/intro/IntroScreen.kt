package com.heklast.smartspender.feature.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.smartspender.project.core.AppColors
import smartspender.composeapp.generated.resources.Res
import smartspender.composeapp.generated.resources.light
import androidx.compose.runtime.*
import com.heklast.smartspender.core.data.remote.ApiService
import kotlinx.coroutines.launch


@Composable
fun IntroScreen(onTimeout: () -> Unit, apiService: ApiService = ApiService()) {
    var advice by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("Loading advice...") }
    LaunchedEffect(Unit) {
        try {
            advice = apiService.getRandomAdvice()
        } catch (e: Exception) {
            advice = "Failed to fetch advice."
        }

        // Existing timeout
        kotlinx.coroutines.delay(3_000)
        onTimeout()
    }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.mint)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.light),
                    contentDescription = "SmartSpender logo",
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SmartSpender",
                    color = AppColors.lightGreen,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Let's be smarter together!",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth().padding(30.dp),
                color = AppColors.lightGreen.copy(alpha = 0.9f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = advice,
                color = AppColors.lightGreen.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
