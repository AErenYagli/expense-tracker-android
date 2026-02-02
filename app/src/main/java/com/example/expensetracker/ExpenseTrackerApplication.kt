package com.example.expensetracker

import android.app.Application
import com.example.expensetracker.data.local.database.ExpenseDatabase
import com.example.expensetracker.data.repository.ExpenseRepository

/**
 * Application class for initializing app-wide dependencies.
 *
 * Why Application class?
 * - Single place for dependency initialization
 * - Lives throughout app lifecycle
 * - Provides dependencies to activities/fragments
 */
class ExpenseTrackerApplication : Application() {

    // Lazy initialization - created only when accessed
    private val database by lazy {
        ExpenseDatabase.getDatabase(this)
    }

    val repository by lazy {
        ExpenseRepository(database.expenseDao())
    }
}