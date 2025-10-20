package com.heklast.smartspender.feature.expense.list

import com.heklast.smartspender.core.common.Result
import com.heklast.smartspender.core.data.remote.dto.CreateExpenseRequest
import com.heklast.smartspender.core.data.remote.dto.ExpenseResponse
import com.heklast.smartspender.core.di.Services
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExpensesListViewModel(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {
    private val api = Services.expenseApi

    private val _items = MutableStateFlow<List<ExpenseResponse>>(emptyList())
    val items: StateFlow<List<ExpenseResponse>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        refresh()
    }



    fun refresh() {
        _loading.value = true
        _error.value = null
        scope.launch {
            when (val r = api.list(limit = 50, offset = 0)) {
                is Result.Ok  -> _items.value = r.value.items
                is Result.Err -> _error.value = r.cause.message ?: "Failed to load expenses"
            }
            _loading.value = false
        }
    }
    fun update(edited: ExpenseResponse, onDone: (Boolean) -> Unit = {}) {
        scope.launch {
            val req = CreateExpenseRequest(
                title = edited.title,
                amount = edited.amount,
                dateEpochMs = edited.dateEpochMs,
                category = edited.category,
                notes = edited.notes,
                tags = edited.tags
            )
            when (api.update(edited.id, req)) {
                is Result.Ok -> {
                    // Replace item locally so UI refreshes immediately
                    _items.value = _items.value.map { if (it.id == edited.id) edited else it }
                    onDone(true)
                }
                is Result.Err -> {
                    _error.value = "Update failed"
                    onDone(false)
                }
            }
        }}

    fun delete(id: String) {
        scope.launch {
            when (api.delete(id)) {
                is Result.Ok  -> _items.value = _items.value.filterNot { it.id == id }
                is Result.Err -> _error.value = "Delete failed"
            }
        }
    }

    fun clearError() { _error.value = null }
}
