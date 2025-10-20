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
import com.heklast.smartspender.responsive.rememberWindowSize
import com.heklast.smartspender.responsive.rememberDimens
import com.heklast.smartspender.responsive.WidthClass

@Preview
@Composable
fun StatisticsScreen() {
    val win = rememberWindowSize()
    val dims = rememberDimens(win)

    val titleSize = when (win.width) {
        WidthClass.Compact -> 22.sp
        WidthClass.Medium -> 26.sp
        WidthClass.Expanded -> 30.sp
    }
    val cardWidth = when (win.width) {
        WidthClass.Compact -> 0.9f
        WidthClass.Medium -> 0.75f
        WidthClass.Expanded -> 0.6f
    }

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
                top = dims.gap * 6,
                start = dims.padding,
                end = dims.padding,
                bottom = dims.gap * 8
            )
        ) {
            item {
                Text(
                    "Analysis",
                    modifier = Modifier.fillMaxWidth().padding(bottom = dims.gap * 3),
                    color = AppColors.black.copy(alpha = 0.9f),
                    fontSize = titleSize,
                    fontWeight = FontWeight.W600,
                    textAlign = TextAlign.Center
                )
            }

            if (pie.isNotEmpty()) {
                items(pie.size) { index ->
                    val slice = pie[index]
                    Row(
                        Modifier.fillMaxWidth(cardWidth).padding(bottom = dims.gap),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(slice.category.name, fontWeight = FontWeight.W500)
                        Text("€${slice.value}")
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(cardWidth)
                        .background(AppColors.white, shape = RoundedCornerShape(70.dp))
                        .padding(dims.padding),
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
                Spacer(Modifier.height(dims.gap * 8))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}