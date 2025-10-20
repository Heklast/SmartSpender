package com.heklast.smartspender.feature.About

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import com.heklast.smartspender.responsive.WidthClass
import com.heklast.smartspender.responsive.rememberDimens
import com.heklast.smartspender.responsive.rememberWindowSize
import org.smartspender.project.core.AppColors

@Composable
fun AboutScreen() {
    val win = rememberWindowSize()
    val dims = rememberDimens(win)
    val isWide = win.width >= WidthClass.Medium

    val titleSize = if (isWide) 28.sp else 20.sp
    val bodySize  = if (isWide) 17.sp else 15.sp
    val cardCorner = dims.corner

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.mint)
    ) {
        // Center content on tablets, full width on phones
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isWide) dims.padding else 0.dp),
            contentAlignment = if (isWide) Alignment.TopCenter else Alignment.TopStart
        ) {
            val maxCardWidth = if (isWide) 720.dp else Dp.Unspecified

            Surface(
                color = AppColors.white,
                shape = RoundedCornerShape(topStart = 70.dp, topEnd = 70.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (maxCardWidth != Dp.Unspecified) Modifier.width(maxCardWidth) else Modifier)
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dims.padding, vertical = dims.padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(bottom = dims.padding * 2)
                ) {
                    item {
                        Text(
                            "About SmartSpender",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = dims.gap * 4),
                            color = AppColors.black.copy(alpha = 0.9f),
                            fontSize = titleSize,
                            fontWeight = FontWeight.W600,
                            textAlign = TextAlign.Center
                        )
                    }

                    item {
                        Text(
                            (
                                    "SmartSpender is your personal finance companion designed to help you take control of your spending with ease and clarity.\n\n" +
                                            "Track your expenses, analyze your spending habits, and gain insights into where your money goes — all in one simple, intuitive interface.\n\n" +
                                            "With SmartSpender, you can:\n\n" +
                                            "• Record and categorize your daily expenses effortlessly\n" +
                                            "• View interactive charts and statistics that show how you spend\n" +
                                            "• Manage your personal profile and track financial progress over time\n" +
                                            "• Keep your data secure and synced through Firebase integration\n\n" +
                                            "Whether you’re saving for a goal, budgeting monthly, or just curious about your habits, SmartSpender gives you the tools to spend smarter and live better."
                                    ),
                            modifier = Modifier.fillMaxWidth(),
                            color = AppColors.black.copy(alpha = 0.9f),
                            fontSize = bodySize,
                            fontWeight = FontWeight.W400,
                            textAlign = if (isWide) TextAlign.Start else TextAlign.Center,
                            lineHeight = (bodySize.value * 1.3f).sp
                        )
                    }

                    // bottom breathing room so BottomBar doesn't overlap
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}