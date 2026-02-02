package com.example.expensetracker.ui.expenselist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.databinding.ItemExpenseBinding
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.utils.Constants

/**
 * RecyclerView Adapter for displaying expense list.
 *
 * Uses ListAdapter with DiffUtil for efficient updates
 */
class ExpenseAdapter(
    private val onItemClick: (Expense) -> Unit,
    private val onItemLongClick: (Expense) -> Boolean
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExpenseViewHolder(
        private val binding: ItemExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Click listeners
            binding.root.setOnClickListener {
                val position = adapterPosition  // ✅ bindingAdapterPosition yerine adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.root.setOnLongClickListener {
                val position = adapterPosition  // ✅ bindingAdapterPosition yerine adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClick(getItem(position))
                } else {
                    false
                }
            }
        }

        fun bind(expense: Expense) {
            binding.apply {
                // Set category name
                tvCategory.text = expense.category

                // Set category icon (first letter)
                tvCategoryIcon.text = expense.category.first().uppercase()

                // Set date
                tvDate.text = expense.formattedDate

                // Set amount
                tvAmount.text = expense.formattedAmount

                // Set category icon background color based on category
                val colorIndex = Constants.EXPENSE_CATEGORIES
                    .indexOf(expense.category)
                    .coerceIn(0, Constants.CHART_COLORS.size - 1)

                categoryIconBackground.setBackgroundColor(
                    Constants.CHART_COLORS[colorIndex]
                )
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates
     */
    private class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
}