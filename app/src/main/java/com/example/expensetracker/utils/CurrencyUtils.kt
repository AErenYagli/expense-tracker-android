package com.example.expensetracker.utils

import com.example.expensetracker.utils.Constants.CURRENCY_SYMBOL
import java.text.DecimalFormat

/**
 * Utility functions for currency formatting
 */
object CurrencyUtils {

    private val currencyFormat = DecimalFormat("#,##0.00")

    /**
     * Format amount with currency symbol
     * Example: 1234.56 -> "₺1,234.56"
     */
    fun formatAmount(amount: Double): String {
        return "$CURRENCY_SYMBOL${currencyFormat.format(amount)}"
    }

    /**
     * Parse string to double safely
     */
    fun parseAmount(amountString: String): Double? {
        return try {
            amountString.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }
}