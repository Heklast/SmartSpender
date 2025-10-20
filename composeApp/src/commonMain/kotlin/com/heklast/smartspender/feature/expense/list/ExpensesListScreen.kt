 package com.heklast.smartspender.feature.expense.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heklast.smartspender.core.data.remote.dto.ExpenseResponse
import com.heklast.smartspender.feature.expense.list.ExpensesListViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import org.smartspender.project.core.AppColors
import com.heklast.smartspender.feature.expense.list.EditExpenseDialog
import com.heklast.smartspender.feature.expense.list.ExpenseRow


@Composable
fun ExpensesListScreen(
    vm: ExpensesListViewModel,
    onAddClick: () -> Unit,
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    // 🔹 which expense we are editing
    var editing by remember { mutableStateOf<ExpenseResponse?>(null) }

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

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 72.dp)
                    ) {
                        items(items, key = { it.id }) { e ->
                            ExpenseRow(
                                e = e,
                                onUpdate = { editing = it },            // 🔹 open dialog
                                onDelete = { vm.delete(e.id) }
                            )
                        }

                        // bottom room for nav/fab
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }

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

    // 🔹 Edit dialog
    editing?.let { current ->
        EditExpenseDialog(
            initial = current,
            onDismiss = { editing = null },
            onSave = { edited ->
                vm.update(edited) { ok ->
                    if (ok) editing = null
                }
            }
        )
    }
}