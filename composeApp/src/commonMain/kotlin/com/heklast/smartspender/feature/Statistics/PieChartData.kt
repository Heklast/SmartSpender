package com.heklast.smartspender.feature.Statistics

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heklast.smartspender.core.domain.model.ExpenseCategory
import org.smartspender.project.core.AppColors
import kotlin.math.roundToInt

// Input: NO color, use Double to match your demo literals
data class InputSlice(
    val category: ExpenseCategory,
    val value: Double
)

private data class ColoredSlice(
    val category: ExpenseCategory,
    val value: Double,
    val color: Color
)

private val defaultPalette = listOf(
    AppColors.lightGreen, AppColors.mint, AppColors.darkGreen, AppColors.black, AppColors.white
)

@Composable
fun PieChart(
    dataRaw: List<InputSlice>,
    title: String,
    modifier: Modifier = Modifier,
    chartSize: Dp = 220.dp,
    legendItemHeight: Dp = 18.dp,
    legendGap: Dp = 8.dp
) {
    val data: List<ColoredSlice> = remember(dataRaw) {
        dataRaw.filter { it.value > 0.0 }.mapIndexed { idx, d ->
            ColoredSlice(
                category = d.category,
                value = d.value,
                color = defaultPalette[idx % defaultPalette.size]
            )
        }
    }

    val total: Double = remember(data) { data.sumOf { it.value } }
    val percents = remember(data, total) {
        if (total <= 0.0) emptyList()
        else data.map { it to (it.value / total) } // fraction as Double
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            Crossfade(targetState = percents) { slices ->
                if (slices.isEmpty()) {
                    Box(
                        modifier = Modifier.size(chartSize),
                        contentAlignment = Alignment.Center
                    ) { Text("No data") }
                } else {
                    Canvas(modifier = Modifier.size(chartSize)) {
                        val diameter = size.minDimension
                        val rectSize = Size(diameter, diameter)
                        var startAngle = -90f

                        slices.forEach { (item, fraction) ->
                            val sweep = (360.0 * fraction).toFloat()
                            drawArc(
                                color = item.color,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = true,
                                size = rectSize
                            )
                            startAngle += sweep
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                data.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(legendItemHeight)
                                .background(item.color, RoundedCornerShape(4.dp))
                        )
                        val pct = if (total > 0.0) (item.value * 100 / total).roundToInt() else 0
                        Text("${item.category} — $pct% (${item.value})", fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(legendGap))
        }
    }
}