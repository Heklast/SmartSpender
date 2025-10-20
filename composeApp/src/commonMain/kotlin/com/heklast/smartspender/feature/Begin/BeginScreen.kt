package org.smartspender.project.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heklast.smartspender.core.data.remote.ApiService
import com.heklast.smartspender.navigation.AppState
import com.heklast.smartspender.navigation.Route
import org.jetbrains.compose.resources.painterResource
import org.smartspender.project.core.AppColors
import smartspender.composeapp.generated.resources.Res
import smartspender.composeapp.generated.resources.darkgreen_logo

// Responsive helpers
import com.heklast.smartspender.responsive.rememberWindowSize
import com.heklast.smartspender.responsive.rememberDimens
import com.heklast.smartspender.responsive.WidthClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginScreen(
    appState: AppState,
    apiService: ApiService = ApiService()
) {
    val win = rememberWindowSize()
    val dims = rememberDimens(win)

    // Scale bits by width class
    val logoWidthFraction = when (win.width) {
        WidthClass.Compact  -> 0.45f
        WidthClass.Medium   -> 0.35f
        WidthClass.Expanded -> 0.25f
    }
    val titleSize = when (win.width) {
        WidthClass.Compact  -> 32.sp
        WidthClass.Medium   -> 38.sp
        WidthClass.Expanded -> 44.sp
    }
    val bodySize = when (win.width) {
        WidthClass.Compact  -> 15.sp
        WidthClass.Medium   -> 17.sp
        WidthClass.Expanded -> 18.sp
    }

    var advice by remember { mutableStateOf("Loading advice...") }
    LaunchedEffect(Unit) {
        advice = runCatching { apiService.getRandomAdvice() }
            .getOrElse { "Failed to fetch advice: ${it.message ?: it::class.simpleName}" }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.lightGreen)
            .padding(horizontal = dims.padding) // side gutters scale with size
    ) {
        // Top / center content
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
                    .fillMaxWidth(logoWidthFraction)
                    .aspectRatio(1f)
            )

            Spacer(Modifier.height(dims.gap * 2))

            Text(
                text = "SmartSpender",
                color = AppColors.darkGreen,
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(dims.gap * 2))

            Text(
                text = "Get to know your habits.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dims.gap / 2),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = bodySize,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Let's start our Smart Journey together!",
                modifier = Modifier.fillMaxWidth(),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = bodySize,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(dims.gap))

            Text(
                text = advice,
                color = AppColors.black.copy(alpha = 0.8f),
                fontSize = bodySize,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(dims.gap * 2))

            Button(
                onClick = { appState.navigate(Route.SignUp) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.mint,
                    contentColor = AppColors.black
                ),
                modifier = Modifier
                    .fillMaxWidth(
                        when (win.width) {
                            WidthClass.Compact  -> 0.6f
                            WidthClass.Medium   -> 0.5f
                            WidthClass.Expanded -> 0.4f
                        }
                    )
            ) {
                Text("Sign Up", fontSize = bodySize)
            }
        }

        // Bottom actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = dims.padding, vertical = dims.gap * 2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Already have an account?",
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = (bodySize.value - 5).sp
            )
            Spacer(Modifier.height(dims.gap))
            Button(
                onClick = { appState.navigate(Route.LogIn) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.mint,
                    contentColor = AppColors.black
                ),
                modifier = Modifier
                    .fillMaxWidth(
                        when (win.width) {
                            WidthClass.Compact  -> 0.6f
                            WidthClass.Medium   -> 0.5f
                            WidthClass.Expanded -> 0.4f
                        }
                    )
            ) {
                Text("Log In", fontSize = bodySize)
            }
        }
    }
}