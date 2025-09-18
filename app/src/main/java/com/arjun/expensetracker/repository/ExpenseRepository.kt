package com.arjun.expensetracker.repository

import com.arjun.expensetracker.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    fun getAllExpenses(): Flow<List<Expense>>

    fun getExpenseById(id: Long): Flow<Expense?>

    fun getTotalAmount(): Flow<Double>

    fun getTotalSpentToday(): Flow<Double>

    suspend fun addExpense(expense: Expense)

    suspend fun updateExpense(expense: Expense)

    suspend fun deleteExpense(expense: Expense)
}
