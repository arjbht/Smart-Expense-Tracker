package com.arjun.expensetracker.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String? = null,
    val receiptImage: String? = null, // Store URI as String
    val timestamp: Long = System.currentTimeMillis()
)