package com.example.expensetracker.ui.addexpense

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.ExpenseTrackerApplication
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentAddExpenseBinding
import com.example.expensetracker.utils.Constants
import com.example.expensetracker.utils.CurrencyUtils
import com.example.expensetracker.utils.DateUtils
import com.example.expensetracker.viewmodel.ExpenseViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

/**
 * Fragment for adding new expenses.
 *
 * Features:
 * - Input amount
 * - Select category from dropdown
 * - Pick date
 * - Add optional note
 * - Validation before saving
 */
class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ExpenseViewModel
    private var selectedDateTimestamp: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupToolbar()
        setupCategorySpinner()
        setupDatePicker()
        setupSaveButton()
    }

    /**
     * Initialize ViewModel
     */
    private fun setupViewModel() {
        val application = requireActivity().application as ExpenseTrackerApplication
        val factory = ExpenseViewModelFactory(application.repository)
        viewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]
    }

    /**
     * Setup toolbar with back navigation
     */
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * Setup category dropdown with predefined categories
     */
    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            Constants.EXPENSE_CATEGORIES
        )
        binding.spinnerCategory.setAdapter(adapter)
    }

    /**
     * Setup date picker dialog
     */
    private fun setupDatePicker() {
        // Set initial date to today
        binding.etDate.setText(DateUtils.formatDate(selectedDateTimestamp))

        binding.etDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    /**
     * Show date picker dialog
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateTimestamp

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDateTimestamp = calendar.timeInMillis
                binding.etDate.setText(DateUtils.formatDate(selectedDateTimestamp))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * Setup save button with validation
     */
    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                saveExpense()
            }
        }
    }

    /**
     * Validate user input
     */
    private fun validateInput(): Boolean {
        val amountText = binding.etAmount.text.toString()
        val category = binding.spinnerCategory.text.toString()

        // Validate amount
        if (amountText.isBlank()) {
            binding.tilAmount.error = "Please enter amount"
            return false
        }

        val amount = CurrencyUtils.parseAmount(amountText)
        if (amount == null || amount <= 0) {
            binding.tilAmount.error = "Please enter valid amount"
            return false
        }

        binding.tilAmount.error = null

        // Validate category
        if (category.isBlank()) {
            binding.tilCategory.error = "Please select category"
            return false
        }

        if (category !in Constants.EXPENSE_CATEGORIES) {
            binding.tilCategory.error = "Please select valid category"
            return false
        }

        binding.tilCategory.error = null

        return true
    }

    /**
     * Save expense to database
     */
    private fun saveExpense() {
        val amount = CurrencyUtils.parseAmount(binding.etAmount.text.toString())!!
        val category = binding.spinnerCategory.text.toString()
        val note = binding.etNote.text.toString().takeIf { it.isNotBlank() }

        viewModel.addExpense(
            amount = amount,
            category = category,
            date = selectedDateTimestamp,
            note = note
        )

        Snackbar.make(
            binding.root,
            "Expense added successfully",
            Snackbar.LENGTH_SHORT
        ).show()

        // Navigate back
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}