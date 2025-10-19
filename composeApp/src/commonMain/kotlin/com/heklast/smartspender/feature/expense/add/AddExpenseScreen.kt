package com.heklast.smartspender.feature.expense.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.heklast.smartspender.core.domain.model.ExpenseCategory

@Composable
fun AddExpenseScreen(
    vm: ExpenseFormViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val state by vm.state.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Add Expense") })
    }) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            OutlinedTextField(
                value = state.title, onValueChange = { vm.update { it.copy(title = it) } },
                label = { Text("Title") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.amountText, onValueChange = { vm.update { s -> s.copy(amountText = it) } },
                label = { Text("Amount") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            // Very simple category picker (dropdown can be added later)
            var cat by remember { mutableStateOf(state.category) }
            LaunchedEffect(cat) { vm.update { it.copy(category = cat) } }
            Text("Category: ${cat.name}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExpenseCategory.values().take(4).forEach { c ->
                    FilterChip(selected = cat == c, onClick = { cat = c }, label = { Text(c.name) })
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.notes, onValueChange = { vm.update { it.copy(notes = it) } },
                label = { Text("Notes") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            if (state.error != null) Text(state.error!!, color = MaterialTheme.colorScheme.error)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(enabled = !state.saving, onClick = { vm.save { onSaved() } }) { Text("Save") }
                OutlinedButton(onClick = onCancel) { Text("Cancel") }
            }
        }
    }
}
