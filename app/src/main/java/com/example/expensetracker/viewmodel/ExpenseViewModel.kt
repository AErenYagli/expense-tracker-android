package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.entity.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.toDomain
import com.example.expensetracker.utils.CurrencyUtils
import com.example.expensetracker.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for managing expense-related UI state and business logic.
 *
 * Responsibilities:
 * - Fetch data from repository
 * - Transform data for UI consumption
 * - Handle user actions
 * - Manage UI state
 */
class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    // Private mutable state
    private val _uiState = MutableStateFlow<ExpenseUiState>(ExpenseUiState.Loading)

    // Public immutable state for UI observation
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    // Statistics state
    private val _statisticsState = MutableStateFlow(StatisticsUiState())
    val statisticsState: StateFlow<StatisticsUiState> = _statisticsState.asStateFlow()

    init {
        // Load expenses when ViewModel is created
        loadExpenses()
    }

    /**
     * Load all expenses with formatted data
     */
    fun loadExpenses() {
        viewModelScope.launch {
            try {
                repository.getAllExpenses()
                    .catch { exception ->
                        _uiState.value = ExpenseUiState.Error(
                            exception.message ?: "Failed to load expenses"
                        )
                    }
                    .collect { expenseEntities ->
                        if (expenseEntities.isEmpty()) {
                            _uiState.value = ExpenseUiState.Empty
                        } else {
                            // Convert entities to domain models with formatting
                            val expenses = expenseEntities.map { entity ->
                                entity.toDomain().copy(
                                    formattedAmount = CurrencyUtils.formatAmount(entity.amount),
                                    formattedDate = DateUtils.formatDate(entity.date)
                                )
                            }

                            // Calculate monthly total
                            val monthlyTotal = calculateMonthlyTotal(expenseEntities)

                            _uiState.value = ExpenseUiState.Success(
                                expenses = expenses,
                                monthlyTotal = monthlyTotal
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = ExpenseUiState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    /**
     * Load expenses for specific month
     */
    fun loadExpensesByMonth(year: Int, month: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = ExpenseUiState.Loading

                val startOfMonth = DateUtils.getStartOfMonth(year, month)
                val endOfMonth = DateUtils.getEndOfMonth(year, month)

                repository.getExpensesByMonth(startOfMonth, endOfMonth)
                    .catch { exception ->
                        _uiState.value = ExpenseUiState.Error(
                            exception.message ?: "Failed to load expenses"
                        )
                    }
                    .collect { expenseEntities ->
                        if (expenseEntities.isEmpty()) {
                            _uiState.value = ExpenseUiState.Empty
                        } else {
                            val expenses = expenseEntities.map { entity ->
                                entity.toDomain().copy(
                                    formattedAmount = CurrencyUtils.formatAmount(entity.amount),
                                    formattedDate = DateUtils.formatDate(entity.date)
                                )
                            }

                            val monthlyTotal = expenseEntities.sumOf { it.amount }

                            _uiState.value = ExpenseUiState.Success(
                                expenses = expenses,
                                monthlyTotal = monthlyTotal
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = ExpenseUiState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    /**
     * Add new expense
     */
    fun addExpense(
        amount: Double,
        category: String,
        date: Long,
        note: String?
    ) {
        viewModelScope.launch {
            try {
                val expense = ExpenseEntity(
                    amount = amount,
                    category = category,
                    date = date,
                    note = note
                )
                repository.insertExpense(expense)
                // No need to reload, Flow will automatically update UI
            } catch (e: Exception) {
                _uiState.value = ExpenseUiState.Error(
                    "Failed to add expense: ${e.message}"
                )
            }
        }
    }

    /**
     * Delete expense
     */
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(
                    ExpenseEntity(
                        id = expense.id,
                        amount = expense.amount,
                        category = expense.category,
                        date = expense.date,
                        note = expense.note
                    )
                )
                // Flow will automatically update UI
            } catch (e: Exception) {
                _uiState.value = ExpenseUiState.Error(
                    "Failed to delete expense: ${e.message}"
                )
            }
        }
    }

    /**
     * Load statistics data
     */
    fun loadStatistics() {
        viewModelScope.launch {
            try {
                _statisticsState.value = _statisticsState.value.copy(isLoading = true)

                val startOfMonth = DateUtils.getCurrentMonthStart()
                val endOfMonth = DateUtils.getCurrentMonthEnd()

                // Combine multiple flows
                combine(
                    repository.getCategoryTotals(),
                    repository.getMonthlyTotal(startOfMonth, endOfMonth)
                ) { categoryTotals, monthlyTotal ->
                    Pair(categoryTotals, monthlyTotal ?: 0.0)
                }.collect { (categoryTotals, monthlyTotal) ->

                    // Calculate average daily expense
                    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    val averageDaily = if (currentDay > 0) monthlyTotal / currentDay else 0.0

                    // Find top category
                    val topCategory = categoryTotals.maxByOrNull { it.value }?.toPair()

                    _statisticsState.value = StatisticsUiState(
                        isLoading = false,
                        categoryTotals = categoryTotals,
                        monthlyTotal = monthlyTotal,
                        averageDaily = averageDaily,
                        topCategory = topCategory,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _statisticsState.value = _statisticsState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load statistics: ${e.message}"
                )
            }
        }
    }

    /**
     * Calculate monthly total from expense list
     */
    private fun calculateMonthlyTotal(expenses: List<ExpenseEntity>): Double {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return expenses.filter { expense ->
            calendar.timeInMillis = expense.date
            calendar.get(Calendar.MONTH) == currentMonth &&
                    calendar.get(Calendar.YEAR) == currentYear
        }.sumOf { it.amount }
    }
}