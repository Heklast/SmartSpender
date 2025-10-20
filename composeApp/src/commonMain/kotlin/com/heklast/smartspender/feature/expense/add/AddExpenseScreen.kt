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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

// responsive helpers
import com.heklast.smartspender.responsive.rememberWindowSize
import com.heklast.smartspender.responsive.rememberDimens
import com.heklast.smartspender.responsive.WidthClass

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseScreen(
    vm: ExpenseFormViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val state by vm.state.collectAsState()

    // responsiveness
    val win = rememberWindowSize()
    val dims = rememberDimens(win)

    val titleSize = when (win.width) {
        WidthClass.Compact  -> 20.sp
        WidthClass.Medium   -> 24.sp
        WidthClass.Expanded -> 28.sp
    }
    val formWidthFraction = when (win.width) {
        WidthClass.Compact  -> 0.92f
        WidthClass.Medium   -> 0.75f
        WidthClass.Expanded -> 0.60f
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
                text = "Add Expense",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dims.gap * 3),
                color = AppColors.black.copy(alpha = 0.9f),
                fontSize = titleSize,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.white, shape = RoundedCornerShape(70.dp))
            ) {
                // make scrollable + center-width content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(
                        start = dims.padding,
                        end = dims.padding,
                        top = dims.gap * 2,
                        bottom = dims.gap * 8 // room for bottom bar
                    )
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(formWidthFraction)
                                .padding(horizontal = 4.dp),
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

                            Spacer(Modifier.height(dims.gap))

                            // Category
                            Text(
                                text = "Category",
                                color = AppColors.black.copy(alpha = 0.9f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                            )

                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = dims.gap),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ExpenseCategory.values().forEach { c ->
                                    FilterChip(
                                        selected = state.category == c,
                                        onClick = { vm.update { s -> s.copy(category = c) } },
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
                            state.error?.let { err ->
                                Spacer(Modifier.height(dims.gap))
                                Text(
                                    text = err,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(Modifier.height(dims.gap * 2))

                            // Actions
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = onCancel,
                                    modifier = Modifier.weight(1f)
                                ) { Text("Cancel") }

                                Button(
                                    enabled = !state.saving,
                                    onClick = { vm.save { onSaved() } },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AppColors.mint,
                                        contentColor = AppColors.black
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(if (state.saving) "Saving…" else "Save")
                                }
                            }
                        }
                    }

                    // extra spacer bottom
                    item { Spacer(Modifier.height(dims.gap * 6)) }
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