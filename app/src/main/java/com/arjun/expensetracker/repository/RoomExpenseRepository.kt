package com.arjun.expensetracker.repository

import com.arjun.expensetracker.data.room.ExpenseDao
import com.arjun.expensetracker.data.room.ExpenseEntity
import com.arjun.expensetracker.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomExpenseRepository(
    private val dao: ExpenseDao
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> =
        dao.getAllExpenses().map { list -> list.map { it.toDomain() } }

    override fun getExpenseById(id: Long): Flow<Expense?> =
        dao.getExpenseById(id).map { it?.toDomain() }

    override fun getTotalAmount(): Flow<Double> =
        dao.getTotalAmount().map { it ?: 0.0 }

    override fun getTotalSpentToday(): Flow<Double> =
        dao.getTotalSpentToday().map { it ?: 0.0 }

    override suspend fun addExpense(expense: Expense) {
        dao.insertExpense(expense.toEntity())
    }

    override suspend fun updateExpense(expense: Expense) {
        dao.updateExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense.toEntity())
    }
}


private fun ExpenseEntity.toDomain() = Expense(
    id = id,
    title = title,
    amount = amount,
    category = category,
    notes = notes,
    receiptImage = receiptImage,
    timestamp = timestamp
)

private fun Expense.toEntity() = ExpenseEntity(
    id = id,
    title = title,
    amount = amount,
    category = category,
    notes = notes,
    receiptImage = receiptImage,
    timestamp = timestamp
)
