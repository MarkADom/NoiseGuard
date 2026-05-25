package com.marcodomingues.noiseguard.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.marcodomingues.noiseguard.domain.model.NoiseLevel
import com.marcodomingues.noiseguard.ui.components.NoiseLineChart
import com.marcodomingues.noiseguard.ui.components.PeriodCard
import com.marcodomingues.noiseguard.ui.components.PeriodStats
import com.marcodomingues.noiseguard.ui.theme.DarkPremium
import com.marcodomingues.noiseguard.ui.theme.NeonColors
import com.marcodomingues.noiseguard.ui.theme.noiseGuardBackground

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val summary = when {
        uiState.averageToday == 0.0 -> "No data yet"
        uiState.averageToday < 40 -> "Today was peaceful"
        uiState.averageToday < 60 -> "Moderate noise today"
        uiState.averageToday < 80 -> "It was a loud day"
        else -> "Dangerously loud today"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .noiseGuardBackground()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Daily summary card
            item {
                DailySummaryCard(
                    summary = summary,
                    averageDb = uiState.averageToday
                )
            }

            // 24-hour chart
            item {
                ChartCard(
                    title = "Last 24 Hours",
                    readings = uiState.last24HoursReadings
                )
            }

            // Summary by period header
            item {
                Text(
                    text = "Summary by Period",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Morning card
            item {
                PeriodCard(
                    emoji = "🌅",
                    title = "Morning (6h-12h)",
                    stats = PeriodStats(
                        averageDb = uiState.morningAverage,
                        peakDb = uiState.morningPeak,
                        peakTime = uiState.morningPeakTime
                    )
                )
            }

            // Afternoon card
            item {
                PeriodCard(
                    emoji = "☀️",
                    title = "Afternoon (12h-18h)",
                    stats = PeriodStats(
                        averageDb = uiState.afternoonAverage,
                        peakDb = uiState.afternoonPeak,
                        peakTime = uiState.afternoonPeakTime
                    )
                )
            }

            // Night card
            item {
                PeriodCard(
                    emoji = "🌙",
                    title = "Night (18h-6h)",
                    stats = PeriodStats(
                        averageDb = uiState.nightAverage,
                        peakDb = uiState.nightPeak,
                        peakTime = uiState.nightPeakTime
                    )
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun DailySummaryCard(
    summary: String,
    averageDb: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkPremium.Surface.copy(alpha = 0.6f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            NeonColors.CyanStart.copy(alpha = 0.1f),
                            NeonColors.MagentaEnd.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "📊", fontSize = 20.sp)
                    Text(
                        text = "\"$summary\"",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Average: ${averageDb.toInt()} dB",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Shows the Vico line chart for the last 24 hours, or an empty state prompt.
 */
@Composable
private fun ChartCard(
    title: String,
    readings: List<NoiseLevel>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkPremium.Surface.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (readings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available\nStart monitoring to see your history",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.4f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                NoiseLineChart(
                    readings = readings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}
