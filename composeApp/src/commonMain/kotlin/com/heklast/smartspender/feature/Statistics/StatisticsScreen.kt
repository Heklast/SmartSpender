package com.heklast.smartspender.feature.Statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

    // Create the VM tied to this uid (your VM already supports testUid)
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
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxSize(0.05f))
            Text(
                "Analysis",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp, 2.dp, 2.dp, 40.dp),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center
            )

            // Optional quick summary above the chart
            pie.forEach { slice ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 70.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(slice.category.name)
                    Text(": ")
                    Text("€${slice.value}")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.white, shape = RoundedCornerShape(70.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        Spacer(modifier = Modifier.height(100.dp))

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
            }
        }
    }
}