package com.heklast.smartspender.feature.expense.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.heklast.smartspender.core.data.remote.dto.ExpenseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ExpensesListScreen(
    vm: ExpensesListViewModel,
    onAddClick: () -> Unit,
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()

    LaunchedEffect(Unit) { vm.refresh() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) { Text("+") }
        }
    ) { p ->
        Column(Modifier.padding(p).fillMaxSize()) {
            if (loading && items.isEmpty()) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(items) { e -> ExpenseRow(e, onDelete = { vm.delete(e.id) }) }
            }
        }
    }
}

@Composable
private fun ExpenseRow(
    e: ExpenseResponse,
    onDelete: () -> Unit
) {
    Card(Modifier.fillMaxWidth().padding(8.dp)) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(e.title, style = MaterialTheme.typography.titleMedium)
                Text("${e.amount} • ${e.category}")
            }
            TextButton(onClick = onDelete) { Text("Delete") }
        }
    }
}
