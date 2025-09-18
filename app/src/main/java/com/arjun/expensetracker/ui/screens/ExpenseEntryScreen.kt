package com.arjun.expensetracker.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arjun.expensetracker.model.Expense
import com.arjun.expensetracker.ui.expense.ExpenseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseEntryScreen(viewModel: ExpenseViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Staff") }
    var customCategory by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var receiptImage by remember { mutableStateOf("") }
    val categories = listOf("Staff", "Travel", "Food", "Utility", "Other")
//    val totalSpentToday by viewModel.totalSpentToday.collectAsState(0.0)
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    var selectedDate by remember { mutableStateOf(Date()) }
    val filteredExpenses = state.expenses.filter {
        dateFormat.format(Date(it.timestamp)) == dateFormat.format(selectedDate)
    }

    val totalAmount = filteredExpenses.sumOf { it.amount }

    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        receiptImage = uri?.toString() ?: ""
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Total Spent Today",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "₹${String.format("%.2f", totalAmount)}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            leadingIcon = { Icon(Icons.Default.Description, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Amount (₹)") },
            leadingIcon = { Icon(Icons.Default.Money, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
        Spacer(Modifier.height(16.dp))

        Text("Category", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { it ->
                SuggestionChip(
                    onClick = { category = it },
                    label = { Text(it) },
                    icon = { Icon(Icons.Default.Category, null) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (category == it) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        labelColor = if (category == it) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        if (category == "Other") {
            OutlinedTextField(
                value = customCategory,
                onValueChange = { customCategory = it },
                label = { Text("Enter Category") },
                leadingIcon = { Icon(Icons.Default.Category, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { if (it.length <= 100) notes = it },
            label = { Text("Notes (optional)") },
            leadingIcon = { Icon(Icons.Default.Description, null) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { galleryLauncher.launch("image/*") }
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Receipt, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (receiptImage.isNotEmpty()) "Receipt Image Added" else "Add Receipt Image",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.AttachFile, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(24.dp))

        // Submit button
        Button(
            onClick = {
                coroutineScope.launch {
                    scale.animateTo(1.05f, animationSpec = tween(120))
                    scale.animateTo(1f, animationSpec = tween(120))
                }
                val finalCategory = if (category == "Other" && customCategory.isNotBlank()) customCategory else category
                viewModel.addExpense(
                    Expense(
                        title = title,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        category = finalCategory,
                        notes = notes.ifBlank { null },
                        receiptImage = receiptImage.ifEmpty { null },
                        timestamp = System.currentTimeMillis()
                    )
                )
                Toast.makeText(context, "Expense Added!", Toast.LENGTH_SHORT).show()
                title = ""
                amount = ""
                notes = ""
                receiptImage = ""
                customCategory = ""
                category = "Staff"
            },
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale.value),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Submit", style = MaterialTheme.typography.titleMedium)
        }
    }
}
