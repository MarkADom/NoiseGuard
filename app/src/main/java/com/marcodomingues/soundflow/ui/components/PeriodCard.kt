package com.marcodomingues.noiseguard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcodomingues.noiseguard.ui.theme.DarkPremium
import com.marcodomingues.noiseguard.ui.theme.NeonColors

/**
 * Summary card for a time period (Morning / Afternoon / Night).
 *
 * @param emoji  Period icon (e.g. "🌅", "☀️", "🌙")
 * @param title  Period label with hours (e.g. "Morning (6h-12h)")
 * @param stats  Average dB, peak dB, and peak time for the period
 */
@Composable
fun PeriodCard(
    emoji: String,
    title: String,
    stats: PeriodStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkPremium.Surface.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: icon and period title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = emoji, fontSize = 28.sp)
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Average: ${stats.averageDb.toInt()} dB",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // Right: peak info
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Peak: ${stats.peakDb.toInt()} dB",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonColors.NeonGreen
                )
                Text(
                    text = "at ${stats.peakTime}",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

data class PeriodStats(
    val averageDb: Double,
    val peakDb: Double,
    val peakTime: String
)
