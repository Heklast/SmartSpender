package com.heklast.smartspender.feature.expense.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heklast.smartspender.core.domain.model.ExpenseCategory
import org.smartspender.project.core.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    vm: ExpenseFormViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val state by vm.state.collectAsState()

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
                text = "Add Expense",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, start = 2.dp, end = 2.dp, bottom = 40.dp),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.white, shape = RoundedCornerShape(70.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    LabeledField(
                        label = "Title",
                        value = state.title,
                        onValueChange = { v -> vm.update { s -> s.copy(title = v) } },
                        placeholder = "Coffee at Café"
                    )

                    // Amount
                    LabeledField(
                        label = "Amount",
                        value = state.amountText,
                        onValueChange = { v -> vm.update { s -> s.copy(amountText = v) } },
                        placeholder = "12.50",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    Spacer(Modifier.height(4.dp))

                    // Category chips
                    Text(
                        text = "Category",
                        color = AppColors.black.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    var cat by remember(state.category) { mutableStateOf(state.category) }
                    LaunchedEffect(cat) { vm.update { s -> s.copy(category = cat) } }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExpenseCategory.values().forEach { c ->
                            FilterChip(
                                selected = cat == c,
                                onClick = { cat = c },
                                label = { Text(c.name) }
                            )
                        }
                    }

                    // Notes
                    LabeledField(
                        label = "Notes",
                        value = state.notes,
                        onValueChange = { v -> vm.update { s -> s.copy(notes = v) } },
                        placeholder = "Optional",
                        singleLine = false,
                        minLines = 3,
                    )

                    // Error
                    if (state.error != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        OutlinedButton(onClick = onCancel) { Text("Cancel") }
                        Button(
                            enabled = !state.saving,
                            onClick = { vm.save { onSaved() } },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.mint, contentColor = AppColors.black)
                        ) { Text(if (state.saving) "Saving…" else "Save") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Text(
        text = label,
        color = AppColors.black.copy(alpha = 0.9f),
        fontSize = 15.sp,
        fontWeight = FontWeight.W500,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        minLines = minLines,
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.lightGreen, shape = RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = keyboardOptions,
        placeholder = { Text(placeholder, color = Color.Gray) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AppColors.lightGreen,
            unfocusedContainerColor = AppColors.lightGreen,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
    Spacer(Modifier.height(14.dp))
}