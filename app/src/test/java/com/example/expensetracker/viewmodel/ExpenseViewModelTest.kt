package com.example.expensetracker.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.expensetracker.data.local.entity.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Unit tests for ExpenseViewModel
 *
 * Simple test example for portfolio demonstration
 */
@ExperimentalCoroutinesApi
class ExpenseViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ExpenseRepository
    private lateinit var viewModel: ExpenseViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(ExpenseRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadExpenses emits Success state when repository returns data`() = runTest {
        // Given
        val expenses = listOf(
            ExpenseEntity(1, 100.0, "Food", System.currentTimeMillis(), null)
        )
        `when`(repository.getAllExpenses()).thenReturn(flowOf(expenses))

        // When
        viewModel = ExpenseViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assert(viewModel.uiState.value is ExpenseUiState.Success)
    }

    @Test
    fun `loadExpenses emits Empty state when repository returns empty list`() = runTest {
        // Given
        `when`(repository.getAllExpenses()).thenReturn(flowOf(emptyList()))

        // When
        viewModel = ExpenseViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assert(viewModel.uiState.value is ExpenseUiState.Empty)
    }
}