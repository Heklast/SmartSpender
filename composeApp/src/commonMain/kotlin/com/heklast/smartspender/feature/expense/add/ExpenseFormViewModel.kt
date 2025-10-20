package com.heklast.smartspender.feature.expense.add

import com.heklast.smartspender.core.common.Result
import com.heklast.smartspender.core.data.remote.dto.CreateExpenseRequest
import com.heklast.smartspender.core.di.Services
import com.heklast.smartspender.core.domain.model.ExpenseCategory
import kotlinx.datetime.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ExpenseFormState(
    val title: String = "",
    val amountText: String = "",
    val dateEpochMs: Long = Clock.System.now().toEpochMilliseconds(),
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val notes: String = "",
    val saving: Boolean = false,
    val error: String? = null
)

class ExpenseFormViewModel(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {
    private val api = Services.expenseApi

    private val _state = MutableStateFlow(ExpenseFormState())
    val state: StateFlow<ExpenseFormState> = _state

    fun update(block: (ExpenseFormState) -> ExpenseFormState) {
        _state.value = block(_state.value)
    }

    /** Reset fields back to defaults/placeholders */
    fun reset() {
        _state.value = ExpenseFormState()
    }

    fun save(onSuccess: (String) -> Unit) {
        val s = _state.value
        val amount = s.amountText.toDoubleOrNull()
        if (s.title.isBlank() || amount == null || amount <= 0.0) {
            _state.value = s.copy(error = "Enter a title and a positive amount")
            return
        }

        _state.value = s.copy(saving = true, error = null)

        scope.launch {
            val req = CreateExpenseRequest(
                title = s.title,
                amount = amount,
                dateEpochMs = s.dateEpochMs,
                category = s.category.name,
                notes = s.notes.ifBlank { null },
                tags = emptyList()
            )
            when (val r = api.create(req)) {
                is Result.Ok -> {
                    // success: clear form and stop "Saving…" label
                    reset()
                    onSuccess(r.value)
                }
                is Result.Err -> {
                    _state.value = _state.value.copy(
                        saving = false,
                        error = r.cause.message ?: "Failed"
                    )
                }
            }
        }
    }
}