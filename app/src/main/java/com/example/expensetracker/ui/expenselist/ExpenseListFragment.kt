package com.example.expensetracker.ui.expenselist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.ExpenseTrackerApplication
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentExpenseListBinding
import com.example.expensetracker.utils.CurrencyUtils
import com.example.expensetracker.viewmodel.ExpenseUiState
import com.example.expensetracker.viewmodel.ExpenseViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Fragment displaying list of expenses with monthly summary.
 *
 * Features:
 * - Display expenses in RecyclerView
 * - Show monthly total
 * - Navigate to add expense screen
 * - Delete expense on long click
 * - Handle different UI states (Loading, Success, Error, Empty)
 */
class ExpenseListFragment : Fragment() {

    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupFab()
        observeUiState()
    }

    /**
     * Initialize ViewModel with repository dependency
     */
    private fun setupViewModel() {
        val application = requireActivity().application as ExpenseTrackerApplication
        val factory = ExpenseViewModelFactory(application.repository)
        viewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]
    }

    /**
     * Setup RecyclerView with adapter
     */
    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(
            onItemClick = { expense ->
                // TODO: Navigate to edit expense (future feature)
                Snackbar.make(
                    binding.root,
                    "Expense: ${expense.category} - ${expense.formattedAmount}",
                    Snackbar.LENGTH_SHORT
                ).show()
            },
            onItemLongClick = { expense ->
                showDeleteConfirmationDialog(expense)
                true
            }
        )

        binding.rvExpenses.apply {
            adapter = expenseAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    /**
     * Setup Floating Action Button for adding expenses
     */
    private fun setupFab() {
        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.addExpenseFragment)
        }
    }

    /**
     * Observe ViewModel UI state and update UI accordingly
     */
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ExpenseUiState.Loading -> {
                        showLoading(true)
                        showEmptyState(false)
                    }

                    is ExpenseUiState.Success -> {
                        showLoading(false)
                        showEmptyState(false)
                        expenseAdapter.submitList(state.expenses)
                        updateMonthlyTotal(state.monthlyTotal)
                    }

                    is ExpenseUiState.Error -> {
                        showLoading(false)
                        showEmptyState(false)
                        Snackbar.make(
                            binding.root,
                            state.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is ExpenseUiState.Empty -> {
                        showLoading(false)
                        showEmptyState(true)
                        updateMonthlyTotal(0.0)
                    }
                }
            }
        }
    }

    /**
     * Show/hide loading indicator
     */
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvExpenses.visibility = if (show) View.GONE else View.VISIBLE
    }

    /**
     * Show/hide empty state
     */
    private fun showEmptyState(show: Boolean) {
        binding.emptyState.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvExpenses.visibility = if (show) View.GONE else View.VISIBLE
    }

    /**
     * Update monthly total display
     */
    private fun updateMonthlyTotal(total: Double) {
        binding.tvMonthlyTotal.text = CurrencyUtils.formatAmount(total)
    }

    /**
     * Show confirmation dialog before deleting expense
     */
    private fun showDeleteConfirmationDialog(expense: com.example.expensetracker.domain.model.Expense) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?\n\n${expense.category}: ${expense.formattedAmount}")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteExpense(expense)
                Snackbar.make(
                    binding.root,
                    "Expense deleted",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}