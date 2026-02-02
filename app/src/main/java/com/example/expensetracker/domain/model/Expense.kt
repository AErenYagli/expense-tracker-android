package com.example.expensetracker.domain.model

import com.example.expensetracker.data.local.entity.ExpenseEntity

/**
 * Domain model for UI layer.
 *
 * Why separate from Entity?
 * - UI needs formatted data (currency, date strings)
 * - Entity should remain pure database model
 * - Easy to add UI-specific fields without touching database
 */
data class Expense(
    val id: Long,
    val amount: Double,
    val category: String,
    val date: Long,
    val note: String?,
    val formattedAmount: String = "", // UI-specific field
    val formattedDate: String = ""     // UI-specific field
)

/**
 * Extension function to convert Entity to Domain model
 */
fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = this.id,
        amount = this.amount,
        category = this.category,
        date = this.date,
        note = this.note
    )
}

/**
 * Extension function to convert Domain to Entity
 */
fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = this.id,
        amount = this.amount,
        category = this.category,
        date = this.date,
        note = this.note
    )
}