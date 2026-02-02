package com.example.expensetracker.utils

import com.example.expensetracker.utils.Constants.DATE_FORMAT
import com.example.expensetracker.utils.Constants.MONTH_YEAR_FORMAT
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions for date operations
 */
object DateUtils {

    private val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat(MONTH_YEAR_FORMAT, Locale.getDefault())

    /**
     * Convert timestamp to formatted date string
     */
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    /**
     * Convert timestamp to month-year string
     */
    fun formatMonthYear(timestamp: Long): String {
        return monthYearFormat.format(Date(timestamp))
    }

    /**
     * Get start of month timestamp
     */
    fun getStartOfMonth(year: Int, month: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * Get end of month timestamp
     */
    fun getEndOfMonth(year: Int, month: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month + 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * Get current month start
     */
    fun getCurrentMonthStart(): Long {
        val calendar = Calendar.getInstance()
        return getStartOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
    }

    /**
     * Get current month end
     */
    fun getCurrentMonthEnd(): Long {
        val calendar = Calendar.getInstance()
        return getEndOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
    }
}