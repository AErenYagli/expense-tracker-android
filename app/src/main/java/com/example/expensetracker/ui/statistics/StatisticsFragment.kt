package com.example.expensetracker.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.ExpenseTrackerApplication
import com.example.expensetracker.databinding.FragmentStatisticsBinding
import com.example.expensetracker.utils.Constants
import com.example.expensetracker.utils.CurrencyUtils
import com.example.expensetracker.viewmodel.ExpenseViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModelFactory
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Fragment displaying expense statistics and charts.
 *
 * Features:
 * - Monthly total
 * - Daily average
 * - Top spending category
 * - Pie chart showing category breakdown
 */
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ExpenseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupPieChart()
        observeStatistics()

        // Load statistics when fragment is created
        viewModel.loadStatistics()
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
     * Setup Pie Chart appearance
     */
    private fun setupPieChart() {
        binding.pieChart.apply {
            // Disable description
            description.isEnabled = false

            // Enable hole
            setDrawHoleEnabled(true)
            setHoleColor(Color.WHITE)
            holeRadius = 50f
            transparentCircleRadius = 55f

            // Center text
            setDrawCenterText(true)
            setCenterTextSize(18f)
            setCenterTextColor(Color.BLACK)

            // Rotation
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true

            // Legend
            legend.isEnabled = true
            legend.textSize = 11f
            legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)

            // Entry labels
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(11f)
            setDrawEntryLabels(false) // Hide labels on slices for cleaner look

            // Use percentage values
            setUsePercentValues(true)
        }
    }

    /**
     * Observe statistics state and update UI
     */
    private fun observeStatistics() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.statisticsState.collect { state ->
                if (state.isLoading) {
                    showLoading(true)
                } else {
                    showLoading(false)

                    if (state.errorMessage != null) {
                        Snackbar.make(
                            binding.root,
                            state.errorMessage,
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        updateStatistics(state)
                    }
                }
            }
        }
    }

    /**
     * Update all statistics displays
     */
    private fun updateStatistics(state: com.example.expensetracker.viewmodel.StatisticsUiState) {
        // Update monthly total
        binding.tvMonthlyTotal.text = CurrencyUtils.formatAmount(state.monthlyTotal)

        // Update daily average
        binding.tvDailyAverage.text = CurrencyUtils.formatAmount(state.averageDaily)

        // Update top category
        binding.tvTopCategory.text = state.topCategory?.first ?: "-"

        // Update pie chart
        updatePieChart(state.categoryTotals)
    }

    /**
     * Update pie chart with category data
     */
    private fun updatePieChart(categoryTotals: Map<String, Double>) {
        if (categoryTotals.isEmpty()) {
            binding.pieChart.clear()
            binding.pieChart.centerText = "No expenses yet"
            return
        }

        val entries = categoryTotals.map { (category, total) ->
            PieEntry(total.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Expenses by Category").apply {
            colors = Constants.CHART_COLORS.toList()
            valueTextSize = 12f
            valueTextColor = Color.WHITE
            sliceSpace = 3f
            selectionShift = 5f
        }

        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(binding.pieChart))
            setValueTextSize(11f)
            setValueTextColor(Color.WHITE)
        }

        binding.pieChart.apply {
            this.data = data
            centerText = "Total\n${CurrencyUtils.formatAmount(categoryTotals.values.sum())}"
            invalidate() // Refresh chart
            animateY(1000)
        }
    }

    /**
     * Show/hide loading indicator
     */
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}