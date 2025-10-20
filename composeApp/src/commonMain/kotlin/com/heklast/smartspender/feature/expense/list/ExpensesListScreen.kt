package com.heklast.smartspender.feature.expense.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heklast.smartspender.core.data.remote.dto.ExpenseResponse
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.smartspender.project.core.AppColors

@Composable
fun ExpensesListScreen(
    vm: ExpensesListViewModel,
    onAddClick: () -> Unit,
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.mint)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxSize(0.05f))

            Text(
                text = "Expenses",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, start = 2.dp, end = 2.dp, bottom = 100.dp),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = AppColors.white,
                        shape = RoundedCornerShape(70.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    // Top row: Refresh + loading
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Your recent expenses",
                            color = AppColors.black.copy(alpha = 0.8f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500
                        )

                        TextButton(
                            enabled = !loading,
                            onClick = { vm.refresh() }
                        ) { Text("Refresh") }
                    }

                    if (loading && items.isEmpty()) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    if (error != null) {
                        // lightweight error chip/banner
                        Surface(
                            color = Color(0xFFFFEAEA),
                            contentColor = Color(0xFF8A0000),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    error ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { vm.refresh() }) { Text("Retry") }
                            }
                        }
                    }

                    // The list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 72.dp) // space for FAB
                    ) {
                        items(items, key = { it.id }) { e ->
                            ExpenseRow(
                                e = e,
                                onDelete = { vm.delete(e.id) }
                            )
                        }
                    }
                }

                // Add FAB
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = AppColors.mint,
                    contentColor = AppColors.black,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) { Text("+") }
            }
        }
    }
}

@Composable
private fun ExpenseRow(
    e: ExpenseResponse,
    onDelete: () -> Unit
) {
    // Pretty amount & date
    val amountText: String = remember(e.amount) { e.amount.toString() } // <- specify type explicitly

    val dateText: String = remember(e.dateEpochMs) {
        val dt = Instant
            .fromEpochMilliseconds(e.dateEpochMs)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        dt.toString() // yyyy-MM-dd
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.lightGreen.copy(alpha = 0.6f))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        e.title,
                        color = AppColors.black,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "$dateText  •  ${e.category}",
                        color = AppColors.black.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    amountText,
                    color = AppColors.black,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFB00020))
                ) {
                    Text("Delete")
                }
            }
        }
    }
}