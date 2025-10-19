package com.heklast.smartspender.feature.About

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smartspender.project.core.AppColors

@Composable
fun AboutScreen() {
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
            Text("About SmartSpender", modifier = Modifier
                .fillMaxWidth().padding(2.dp,2.dp,2.dp,40.dp),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.white, shape = RoundedCornerShape(70.dp))
            ){
        Text("SmartSpender is your personal finance companion designed to help you take control of your spending with ease and clarity.\n" +
                "\n" +
                "Track your expenses, analyze your spending habits, and gain insights into where your money goes — all in one simple, intuitive interface.\n" +
                "\n" +
                "With SmartSpender, you can:\n" +
                "\n" +
                "Record and categorize your daily expenses effortlessly\n" +
                "\n" +
                "View interactive charts and statistics that show how you spend\n" +
                "\n" +
                "Manage your personal profile and track financial progress over time\n" +
                "\n" +
                "Keep your data secure and synced through Firebase integration\n" +
                "\n" +
                "Whether you’re saving for a goal, budgeting monthly, or just curious about your habits, SmartSpender gives you the tools to spend smarter and live better.",
                modifier = Modifier
                .fillMaxWidth().padding(20.dp), color = AppColors.black.copy(alpha = 0.9f),
            fontSize = 15.sp,
            fontWeight = FontWeight.W400,
            textAlign = TextAlign.Center)

        }}
}}
