package com.example.expensetracker.viewmodel

import com.example.expensetracker.domain.model.Expense

/**
 * Sealed class representing different UI states.
 *
 * Why sealed class?
 * - Type-safe state handling
 * - Exhaustive when expressions
 * - Clear state transitions
 * - Easy to add new states
 */
sealed class ExpenseUiState {

    /**
     * Initial state or when loading data
     */
    object Loading : ExpenseUiState()

    /**
     * Data loaded successfully
     * @param expenses List of expenses to display
     * @param monthlyTotal Total expenses for current month
     */
    data class Success(
        val expenses: List<Expense>,
        val monthlyTotal: Double = 0.0
    ) : ExpenseUiState()

    /**
     * Error occurred during data operations
     * @param message Error message to display
     */
    data class Error(val message: String) : ExpenseUiState()

    /**
     * Empty state when no expenses found
     */
    object Empty : ExpenseUiState()
}

/**
 * State for expense statistics screen
 */
data class StatisticsUiState(
    val isLoading: Boolean = false,
    val categoryTotals: Map<String, Double> = emptyMap(),
    val monthlyTotal: Double = 0.0,
    val averageDaily: Double = 0.0,
    val topCategory: Pair<String, Double>? = null,
    val errorMessage: String? = null
)