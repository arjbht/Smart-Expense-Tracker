package com.arjun.expensetracker.ui.screens

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arjun.expensetracker.ui.expense.ExpenseViewModel
import kotlinx.coroutines.launch

@Composable
fun ExpenseReportScreen(viewModel: ExpenseViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val isLight = MaterialTheme.colorScheme.background.luminance() > 0.5f

//    val cardBg = if (isLight) Color.White else Color(0xFF232526)
    val cardBg = MaterialTheme.colorScheme.surfaceContainerLow
//    val cardBg = if (isLight) Color.White.copy(alpha = 0.85f) else Color(0xFF232526).copy(alpha = 0.85f)
    val accent = MaterialTheme.colorScheme.primary

    val dailyTotals by viewModel.dailyTotals().collectAsState(initial = emptyMap())
    val categories by viewModel.categoryTotals().collectAsState(initial = emptyMap())
    val barColors = listOf(
        Color(0xFF1976D2),
        Color(0xFF388E3C),
        Color(0xFFFBC02D),
        Color(0xFFD32F2F),
        Color(0xFF7B1FA2),
        Color(0xFF0288D1),
        Color(0xFF455A64)
    )



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Smart Expense Report",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = accent,
                )
                Spacer(Modifier.height(8.dp))
                Text("Total Expenses", style = MaterialTheme.typography.titleMedium)
                Text(
                    "₹${String.format("%.2f", state.totalAmount)}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Animated Bar Chart
        Text("Last 7 Days", style = MaterialTheme.typography.titleMedium, color = accent)
        AnimatedBarChart(
            data = dailyTotals.values.toList(),
            barColors = barColors,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(vertical = 12.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Category Totals
        Text("Category Totals", style = MaterialTheme.typography.titleMedium, color = accent)
        Spacer(Modifier.height(8.dp))
        categories.entries.forEachIndexed { i, entry ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        entry.key,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = accent
                    )
                    Text(
                        "₹${entry.value}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = barColors[i % barColors.size]
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // Actions
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accent),
                onClick = {
                    coroutineScope.launch {
                        viewModel.exportDataToPdf(context)
                    }
                }
            ) {
                Text("Export PDF", color = MaterialTheme.colorScheme.onPrimary)
            }
            IconButton(
                onClick = {
                    val reportDetails = buildString {
                        append("Expense Report\n")
                        append("Total: ₹${state.totalAmount}\n\n")
                        append("Category Totals:\n")
                        categories.forEach { (category, amount) ->
                            append("- $category: ₹$amount\n")
                        }
                        append("\nLast 7 Days:\n")
                        dailyTotals.forEach { (date, amount) ->
                            append("- $date: ₹$amount\n")
                        }
                    }
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, reportDetails)
                    }
                    startActivity(context, Intent.createChooser(shareIntent, "Share Report"), null)
                }
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = accent)
            }
        }

        Spacer(Modifier.height(16.dp)) // bottom padding
    }
}

@Composable
fun AnimatedBarChart(
    data: List<Double>,
    barColors: List<Color>,
    modifier: Modifier = Modifier
) {
    val daysToShow = 7
    val paddedData = if (data.size < daysToShow) {
        List(daysToShow - data.size) { 0.0 } + data
    } else {
        data.takeLast(daysToShow)
    }
    val max = (paddedData.maxOrNull() ?: 1.0)
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    Row(
        modifier = modifier
            .height(240.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        paddedData.forEachIndexed { i, value ->
            val animatedHeight = (if (max > 0) value / max else 0.0) * 150 * animProgress.value
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "₹${value.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = barColors[i % barColors.size]
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier
                            .height(animatedHeight.dp)
                            .width(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(barColors[i % barColors.size])
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Day ${i - paddedData.size + daysToShow + 1}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}


