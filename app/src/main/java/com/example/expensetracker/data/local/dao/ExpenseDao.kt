package com.example.expensetracker.data.local.dao

import androidx.room.*
import com.example.expensetracker.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for expense operations
 */
@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT * FROM expenses 
        WHERE date >= :startOfMonth AND date < :endOfMonth 
        ORDER BY date DESC
    """)
    fun getExpensesByMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT category, SUM(amount) as total 
        FROM expenses 
        GROUP BY category
    """)
    fun getCategoryTotals(): Flow<List<CategoryTotal>>

    @Query("""
        SELECT SUM(amount) FROM expenses 
        WHERE date >= :startOfMonth AND date < :endOfMonth
    """)
    fun getMonthlyTotal(startOfMonth: Long, endOfMonth: Long): Flow<Double?>

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Long): ExpenseEntity?
}


data class CategoryTotal(
    val category: String,
    val total: Double
)