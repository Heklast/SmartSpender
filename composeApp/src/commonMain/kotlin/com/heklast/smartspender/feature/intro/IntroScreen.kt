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

// responsive helpers
import com.heklast.smartspender.responsive.rememberWindowSize
import com.heklast.smartspender.responsive.rememberDimens
import com.heklast.smartspender.responsive.WidthClass
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun IntroScreen(onTimeout: () -> Unit) {
    // navigate away after 3s
    LaunchedEffect(Unit) {
        delay(3_000)
        onTimeout()
    }

    // responsive tokens
    val win = rememberWindowSize()
    val dims = rememberDimens(win)

    val titleSize = when (win.width) {
        WidthClass.Compact  -> 28.sp
        WidthClass.Medium   -> 34.sp
        WidthClass.Expanded -> 40.sp
    }
    val taglineSize = when (win.width) {
        WidthClass.Compact  -> 16.sp
        WidthClass.Medium   -> 18.sp
        WidthClass.Expanded -> 20.sp
    }
    val logoWidthFraction = when (win.width) {
        WidthClass.Compact  -> 0.45f
        WidthClass.Medium   -> 0.35f
        WidthClass.Expanded -> 0.28f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.mint)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = dims.padding)
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
                    .fillMaxWidth(logoWidthFraction)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(dims.gap * 2))

            Text(
                text = "SmartSpender",
                color = AppColors.lightGreen,
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Let's be smarter together!",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = dims.padding, vertical = dims.gap * 3),
            color = AppColors.lightGreen.copy(alpha = 0.9f),
            fontSize = taglineSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}