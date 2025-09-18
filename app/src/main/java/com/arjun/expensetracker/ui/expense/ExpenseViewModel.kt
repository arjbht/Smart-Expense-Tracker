package com.arjun.expensetracker.ui.expense

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arjun.expensetracker.model.Expense
import com.arjun.expensetracker.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repo: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private val _totalSpentToday = MutableStateFlow(0.0)
    val totalSpentToday: StateFlow<Double> = _totalSpentToday.asStateFlow()

    private val _eventChannel = Channel<ExpenseUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        observeExpenses()
        observeTotalAmount()
        observeTotalSpentToday()
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            repo.getAllExpenses()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message, isLoading = false) } }
                .collect { list ->
                    _uiState.update { it.copy(expenses = list, isLoading = false) }
                }
        }
    }

    private fun observeTotalSpentToday() {
        viewModelScope.launch {
            repo.getTotalSpentToday()
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { total ->
                    _totalSpentToday.value = total
                }
        }
    }

    private fun observeTotalAmount() {
        viewModelScope.launch {
            repo.getTotalAmount()
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { total ->
                    _uiState.update { it.copy(totalAmount = total) }
                }
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repo.addExpense(expense)
                _eventChannel.send(ExpenseUiEvent.ExpenseSaved)
            } catch (e: Exception) {
                _eventChannel.send(ExpenseUiEvent.ShowMessage("Failed: ${e.message}"))
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repo.deleteExpense(expense)
                _eventChannel.send(ExpenseUiEvent.ShowMessage("Deleted successfully"))
            } catch (e: Exception) {
                _eventChannel.send(ExpenseUiEvent.ShowMessage("Delete failed: ${e.message}"))
            }
        }
    }

    fun isDuplicate(title: String, amount: String, category: String): Boolean {
        val amt = amount.toDoubleOrNull() ?: return false
        return _uiState.value.expenses.any {
            it.title.equals(title, ignoreCase = true) &&
                    it.amount == amt &&
                    it.category == category
        }
    }

    fun dailyTotals(): Flow<Map<String, Double>> {
        return repo.getAllExpenses()
            .map { expenses ->
                expenses.groupBy { expense ->
                    val date = java.util.Date(expense.timestamp)
                    val format = java.text.SimpleDateFormat("yyyy-MM-dd")
                    format.format(date)
                }.mapValues { entry ->
                    entry.value.sumOf { it.amount }
                }
            }
    }

    fun categoryTotals(): Flow<Map<String, Double>> {
        return repo.getAllExpenses()
            .map { expenses ->
                expenses.groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }
            }
    }

    suspend fun exportDataToPdf(context: Context): Uri? {
        val expenses = repo.getAllExpenses().first()
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint()
        var y = 25
        canvas.drawText("Expense Report", 10f, y.toFloat(), paint)
        y += 25
        expenses.forEach { expense ->
            canvas.drawText("Title: ${expense.title}", 10f, y.toFloat(), paint)
            y += 20
            canvas.drawText("Amount: ${expense.amount}", 10f, y.toFloat(), paint)
            y += 20
            canvas.drawText("Category: ${expense.category}", 10f, y.toFloat(), paint)
            y += 20
            val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(expense.timestamp))
            canvas.drawText("Date: $date", 10f, y.toFloat(), paint)
            y += 20
            expense.notes?.let {
                canvas.drawText("Notes: $it", 10f, y.toFloat(), paint)
                y += 20
            }
            y += 10
        }
        pdfDocument.finishPage(page)
        val file = java.io.File(context.getExternalFilesDir(null), "expenses.pdf")
        pdfDocument.writeTo(java.io.FileOutputStream(file))
        pdfDocument.close()
        return androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

}
