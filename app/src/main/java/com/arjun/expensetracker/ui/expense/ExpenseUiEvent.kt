package com.arjun.expensetracker.ui.expense

sealed class ExpenseUiEvent {
    data class ShowMessage(val message: String) : ExpenseUiEvent()
    object ExpenseSaved : ExpenseUiEvent()
}
