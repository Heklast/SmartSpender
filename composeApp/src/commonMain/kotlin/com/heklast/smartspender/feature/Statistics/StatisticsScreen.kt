package com.heklast.smartspender.feature.Statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heklast.smartspender.features.statistics.StatisticsViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.smartspender.project.core.AppColors
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.navigationBars

@Preview
@Composable
fun StatisticsScreen() {

    // Use the signed-in user's UID
    val uid: String? = Firebase.auth.currentUser?.uid
    if (uid == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(AppColors.mint),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Please sign in to see your statistics.",
                color = AppColors.black,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    // Create the VM tied to this uid
    val vm = remember(uid) { StatisticsViewModel(testUid = uid) }
    LaunchedEffect(uid) { vm.load() }

    val pie by vm.pieData.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.mint)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.mint),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = 48.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 32.dp
            )
        ) {
            item {
                Text(
                    "Analysis",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    color = AppColors.black.copy(alpha = 0.9f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W600,
                    textAlign = TextAlign.Center
                )
            }

            // Show category list summary
            if (pie.isNotEmpty()) {
                items(pie.size) { index ->
                    val slice = pie[index]
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(slice.category.name)
                        Text(": ")
                        Text("€${slice.value}")
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.white, shape = RoundedCornerShape(70.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        loading -> CircularProgressIndicator()
                        error != null -> Text("Error: $error")
                        else -> PieChart(
                            dataRaw = pie,
                            title = "Spending by Category",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}