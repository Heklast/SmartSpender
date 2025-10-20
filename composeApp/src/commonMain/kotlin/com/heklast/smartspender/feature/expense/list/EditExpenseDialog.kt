package com.heklast.smartspender.feature.expense.list


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.style.TextAlign

import com.heklast.smartspender.core.data.remote.dto.ExpenseResponse
import org.smartspender.project.core.AppColors

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.LocalDate

@Composable
fun EditExpenseDialog(
    initial: ExpenseResponse,
    onDismiss: () -> Unit,
    onSave: (ExpenseResponse) -> Unit
) {
    var title by remember { mutableStateOf(initial.title) }
    var amountText by remember { mutableStateOf(initial.amount.toString()) }
    var category by remember { mutableStateOf(initial.category) }
    var notes by remember { mutableStateOf(initial.notes.orEmpty()) }
    var tagsText by remember { mutableStateOf(initial.tags.joinToString(",")) }

    val initialDate = remember(initial.dateEpochMs) {
        Instant.fromEpochMilliseconds(initial.dateEpochMs)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date.toString()
    }
    var dateText by remember { mutableStateOf(initialDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                val parsedEpochMs = runCatching {
                    // very simple parser: expects yyyy-MM-dd
                    kotlinx.datetime.LocalDate.parse(dateText)
                        .atStartOfDayIn(TimeZone.currentSystemDefault())
                        .toEpochMilliseconds()
                }.getOrElse { initial.dateEpochMs }

                val edited = initial.copy(
                    title = title,
                    amount = amount,
                    category = category,
                    notes = notes.ifBlank { null },
                    tags = tagsText.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() },
                    dateEpochMs = parsedEpochMs
                )
                onSave(edited)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Edit expense") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Title") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText, onValueChange = { amountText = it },
                    label = { Text("Amount") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = dateText, onValueChange = { dateText = it },
                    label = { Text("Date (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = category, onValueChange = { category = it },
                    label = { Text("Category") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Notes") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = tagsText, onValueChange = { tagsText = it },
                    label = { Text("Tags (comma-separated)") }, modifier = Modifier.fillMaxWidth()
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
 fun ExpenseRow(
    e: ExpenseResponse,
    onUpdate: (ExpenseResponse) -> Unit,
    onDelete: () -> Unit
) {
    val amountText: String = remember(e.amount) { e.amount.toString() }

    val dateText: String = remember(e.dateEpochMs) {
        val dt = Instant
            .fromEpochMilliseconds(e.dateEpochMs)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        dt.toString()
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
                    onClick = { onUpdate(e) },                         // 🔹 Update
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1E88E5))
                ) { Text("Update") }

                Spacer(Modifier.width(8.dp))

                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFB00020))
                ) { Text("Delete") }
            }
        }
    }
}