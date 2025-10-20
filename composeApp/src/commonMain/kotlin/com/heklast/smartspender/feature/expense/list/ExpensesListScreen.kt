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
import org.smartspender.project.core.AppColors
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.imePadding

// responsive helpers
import com.heklast.smartspender.responsive.rememberWindowSize
import com.heklast.smartspender.responsive.rememberDimens
import com.heklast.smartspender.responsive.WidthClass

@Composable
fun ExpensesListScreen(
    vm: ExpensesListViewModel,
    onAddClick: () -> Unit,
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    // which expense are we editing
    var editing by remember { mutableStateOf<ExpenseResponse?>(null) }

    val win = rememberWindowSize()
    val dims = rememberDimens(win)

    val titleSize = when (win.width) {
        WidthClass.Compact  -> 20.sp
        WidthClass.Medium   -> 24.sp
        WidthClass.Expanded -> 28.sp
    }
    val listWidthFraction = when (win.width) {
        WidthClass.Compact  -> 0.96f
        WidthClass.Medium   -> 0.8f
        WidthClass.Expanded -> 0.7f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.mint)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.fillMaxSize(0.05f))

            Text(
                text = "Expenses",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dims.gap * 5),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = titleSize,
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
                        .padding(horizontal = dims.padding, vertical = dims.gap)
                        .align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(listWidthFraction)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Your recent expenses",
                            color = AppColors.black.copy(alpha = 0.8f),
                            fontSize = when (win.width) {
                                WidthClass.Compact  -> 16.sp
                                WidthClass.Medium   -> 18.sp
                                WidthClass.Expanded -> 20.sp
                            },
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
                                .fillMaxWidth(listWidthFraction)
                                .padding(horizontal = 8.dp)
                        )
                        Spacer(Modifier.height(dims.gap))
                    }

                    error?.let { msg ->
                        Surface(
                            color = Color(0xFFFFEAEA),
                            contentColor = Color(0xFF8A0000),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth(listWidthFraction)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    msg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { vm.refresh() }) { Text("Retry") }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = (dims.padding * (1 - listWidthFraction)).coerceAtLeast(0.dp),
                            end = (dims.padding * (1 - listWidthFraction)).coerceAtLeast(0.dp),
                            top = dims.gap,
                            bottom = dims.gap * 10  // room for FAB & bottom bar
                        )
                    ) {
                        items(items, key = { it.id }) { e ->
                            ExpenseRow(
                                e = e,
                                onUpdate = { editing = it },
                                onDelete = { vm.delete(e.id) }
                            )
                        }
                        item { Spacer(Modifier.height(dims.gap * 8)) }
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

    // Edit dialog
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