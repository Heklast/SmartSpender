package com.heklast.smartspender.feature.expense.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.heklast.smartspender.core.data.remote.dto.ExpenseResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesListScreen(
    vm: ExpensesListViewModel,
    onAddClick: () -> Unit,
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expenses") },
                actions = {
                    TextButton(enabled = !loading, onClick = { vm.refresh() }) {
                        Text("Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) { Text("+") }
        }
    ) { p ->
        Column(
            Modifier
                .padding(p)
                .fillMaxSize()
        ) {
            if (loading && items.isEmpty()) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            if (error != null) {
                AssistChip(
                    onClick = { vm.refresh() },
                    label = { Text(error ?: "") },
                    modifier = Modifier
                        .padding(12.dp)
                )
            }

            LazyColumn(Modifier.fillMaxSize()) {
                items(items) { e ->
                    ExpenseRow(
                        e = e,
                        onDelete = { vm.delete(e.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseRow(
    e: ExpenseResponse,
    onDelete: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(e.title, style = MaterialTheme.typography.titleMedium)
                Text("${e.amount}  •  ${e.category}")
            }
            TextButton(onClick = onDelete) { Text("Delete") }
        }
    }
}
