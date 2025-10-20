// features/statistics/StatisticsViewModel.kt
package com.heklast.smartspender.features.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heklast.smartspender.core.data.ExpensesRepository
import com.heklast.smartspender.core.domain.model.Expense
import com.heklast.smartspender.feature.Statistics.InputSlice   // your chart input (category, value)
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val testUid: String? = null
) : ViewModel() {

    private val _pieData = MutableStateFlow<List<InputSlice>>(emptyList())
    val pieData: StateFlow<List<InputSlice>> = _pieData


    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val uid = testUid ?: Firebase.auth.currentUser?.uid
                if (uid == null) {
                    _error.value = "Not signed in"
                    _pieData.value = emptyList()
                    return@launch
                }

                val expenses: List<Expense> = ExpensesRepository.getExpensesForUser(uid)

                val grouped = expenses
                    .groupBy { it.category }
                    .map { (cat, items) ->
                        InputSlice(category = cat, value = items.sumOf { it.amount })
                    }
                    .sortedByDescending { it.value }

                _pieData.value = grouped
            } catch (t: Throwable) {
                _error.value = t.message ?: "Failed to load expenses"
                _pieData.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}