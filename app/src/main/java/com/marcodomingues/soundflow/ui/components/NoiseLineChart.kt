package com.marcodomingues.noiseguard.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.marcodomingues.noiseguard.domain.model.NoiseLevel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoiseLineChart(
    readings: List<NoiseLevel>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val sampled = remember(readings) {
        if (readings.isEmpty()) emptyList()
        else {
            val step = maxOf(1, readings.size / 50)
            readings.filterIndexed { index, _ -> index % step == 0 }
        }
    }

    LaunchedEffect(sampled) {
        modelProducer.runTransaction {
            if (sampled.isNotEmpty()) {
                lineSeries {
                    series(
                        x = sampled.indices.map { it.toFloat() },
                        y = sampled.map { it.decibels.toFloat() }
                    )
                }
            } else {
                lineSeries { series(listOf(0f)) }
            }
        }
    }

    val xFormatter = remember(sampled) {
        CartesianValueFormatter { value, _, _ ->
            val index = value.toInt()
            if (index >= 0 && index < sampled.size) {
                dateFormat.format(Date(sampled[index].timestamp))
            } else ""
        }
    }

    val whiteLabel = rememberTextComponent(color = Color.White.copy(alpha = 0.8f))
    val greyLine = rememberLineComponent(
        color = Color.White.copy(alpha = 0.15f),
        thickness = 1.dp
    )

    val lineLayer = rememberLineCartesianLayer()

    CartesianChartHost(
        chart = rememberCartesianChart(
            lineLayer,
            startAxis = rememberStartAxis(
                label = whiteLabel,
                guideline = greyLine
            ),
            bottomAxis = rememberBottomAxis(
                label = whiteLabel,
                valueFormatter = xFormatter,
                guideline = greyLine
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}