package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository acts as a single source of truth
 */
class ExpenseRepository(private val expenseDao: ExpenseDao) {

    fun getAllExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses()
    }

    fun getExpensesByMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByMonth(startOfMonth, endOfMonth)
    }

    fun getCategoryTotals(): Flow<Map<String, Double>> {
        return expenseDao.getCategoryTotals().map { list ->
            list.associate { it.category to it.total }
        }
    }

    fun getMonthlyTotal(startOfMonth: Long, endOfMonth: Long): Flow<Double?> {
        return expenseDao.getMonthlyTotal(startOfMonth, endOfMonth)
    }

    suspend fun insertExpense(expense: ExpenseEntity): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun getExpenseById(expenseId: Long): ExpenseEntity? {
        return expenseDao.getExpenseById(expenseId)
    }
}