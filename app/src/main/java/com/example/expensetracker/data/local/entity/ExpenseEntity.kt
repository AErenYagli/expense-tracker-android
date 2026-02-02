package com.example.expensetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database entity representing a single expense record.
 *
 * @property id Unique identifier (auto-generated)
 * @property amount Expense amount in local currency
 * @property category Expense category (e.g., Food, Transport, Shopping)
 * @property date Unix timestamp when expense occurred
 * @property note Optional user note about the expense
 */
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val date: Long,
    val note: String? = null
)