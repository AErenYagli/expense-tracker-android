package com.example.expensetracker.utils

/**
 * App-wide constants
 */
object Constants {

    // Expense Categories
    val EXPENSE_CATEGORIES = listOf(
        "Food & Dining",
        "Transportation",
        "Shopping",
        "Entertainment",
        "Bills & Utilities",
        "Healthcare",
        "Education",
        "Other"
    )

    // Date Format
    const val DATE_FORMAT = "dd MMM yyyy"
    const val MONTH_YEAR_FORMAT = "MMMM yyyy"

    // Currency
    const val CURRENCY_SYMBOL = "₺"

    // Chart Colors
    val CHART_COLORS = intArrayOf(
        0xFF1976D2.toInt(), // Blue
        0xFFE91E63.toInt(), // Pink
        0xFF4CAF50.toInt(), // Green
        0xFFFF9800.toInt(), // Orange
        0xFF9C27B0.toInt(), // Purple
        0xFFF44336.toInt(), // Red
        0xFF00BCD4.toInt(), // Cyan
        0xFFFFEB3B.toInt()  // Yellow
    )
}