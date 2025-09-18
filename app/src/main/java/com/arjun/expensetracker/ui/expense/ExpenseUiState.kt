package com.arjun.expensetracker.ui.expense

import com.arjun.expensetracker.model.Expense

data class ExpenseUiState(
    val expenses: List<Expense> = emptyList(),
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val dailyTotals: List<Double> = emptyList(),
    val categoryTotals: List<String> = emptyList()
)

