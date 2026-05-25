package com.marcodomingues.noiseguard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcodomingues.noiseguard.ui.theme.NeonColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * 270° speedometer-style gauge with a gradient arc and a triangle peak marker.
 *
 * @param currentDb Current decibel reading. AudioAnalyzer outputs [20.0, 120.0];
 *                  values are mapped to a 0-270° sweep using the full 0-100 scale,
 *                  so readings below 20 dB leave the arc empty.
 * @param peakDb    Peak value displayed as a triangle marker and numeric label.
 */
@Composable
fun CircularGaugeMeter(
    currentDb: Double,
    peakDb: Double,
    modifier: Modifier = Modifier
) {
    // 0-100 dB maps to 0-270°; values outside that range are clamped by coerceIn
    val sweepAngle by animateFloatAsState(
        targetValue = (currentDb.toFloat() / 100f) * 270f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "arc_sweep"
    )

    // Stable across recompositions — color stops don't change at runtime
    val neonGradient = remember {
        Brush.sweepGradient(
            colorStops = arrayOf(
                0.0f to Color(0xFF00E5FF),
                0.15f to Color(0xFF00D4E5),
                0.30f to Color(0xFF4DB8FF),
                0.45f to Color(0xFF6B5AFF),
                0.60f to Color(0xFF9B4DFF),
                0.75f to Color(0xFFD946EF),
                0.90f to Color(0xFFE91E63),
                1.0f to Color(0xFFFF1493)
            )
        )
    }

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2
            val centerY = canvasHeight / 2

            // Arc configuration
            val arcStrokeWidth = 48f
            val arcRadius = (canvasWidth / 2) * 0.88f
            val arcRect = Size(arcRadius * 2, arcRadius * 2)
            val arcTopLeft = Offset(centerX - arcRadius, centerY - arcRadius)

            val innerCircleRadius = arcRadius - arcStrokeWidth - 8f

            drawCircle(
                color = Color(0xFF2A2E3F),
                center = Offset(centerX, centerY),
                radius = innerCircleRadius + 4f
            )

            drawCircle(
                color = Color(0xFF0A0E1A),
                center = Offset(centerX, centerY),
                radius = innerCircleRadius
            )

            // Full 270° background track so the unfilled portion is visible
            drawArc(
                color = Color.White.copy(alpha = 0.15f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcRect,
                style = Stroke(
                    width = arcStrokeWidth,
                    cap = StrokeCap.Round
                )
            )

            // Rotate canvas so the gradient aligns with the arc's 135° start angle
            rotate(degrees = 135f, pivot = Offset(centerX, centerY)) {

                // Three glow layers create the neon bloom effect
                drawArc(
                    brush = neonGradient,
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcRect,
                    style = Stroke(width = arcStrokeWidth + 26f, cap = StrokeCap.Round),
                    alpha = 0.3f,
                    blendMode = BlendMode.Screen
                )

                drawArc(
                    brush = neonGradient,
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcRect,
                    style = Stroke(width = arcStrokeWidth + 13f, cap = StrokeCap.Round),
                    alpha = 0.55f,
                    blendMode = BlendMode.Plus
                )

                drawArc(
                    brush = neonGradient,
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcRect,
                    style = Stroke(width = arcStrokeWidth + 6f, cap = StrokeCap.Round),
                    alpha = 0.7f,
                    blendMode = BlendMode.Plus
                )

                drawArc(
                    brush = neonGradient,
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcRect,
                    style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Round),
                    alpha = 1f
                )
            }

            // Peak marker: triangle at the peak dB position along the arc
            val peakAngleInArc = (peakDb.toFloat() / 100f) * 270f
            val peakAngleAbsolute = 135f + peakAngleInArc
            val peakAngleRad = Math.toRadians(peakAngleAbsolute.toDouble())

            // Position the marker inside the arc stroke so it doesn't overlap the track
            val peakMarkerRadius = arcRadius - arcStrokeWidth / 2 - 6f
            val peakX = centerX + (peakMarkerRadius * cos(peakAngleRad)).toFloat()
            val peakY = centerY + (peakMarkerRadius * sin(peakAngleRad)).toFloat()

            // Triangle points outward (toward the arc) from the marker position
            val trianglePath = Path().apply {
                val angleRad = Math.toRadians(peakAngleAbsolute.toDouble())

                val tipOffset = 12f
                val baseWidth = 6f

                val tipX = peakX + (tipOffset * cos(angleRad)).toFloat()
                val tipY = peakY + (tipOffset * sin(angleRad)).toFloat()

                val perpAngle = angleRad + Math.PI / 2
                val leftX = peakX + (baseWidth * cos(perpAngle)).toFloat()
                val leftY = peakY + (baseWidth * sin(perpAngle)).toFloat()
                val rightX = peakX - (baseWidth * cos(perpAngle)).toFloat()
                val rightY = peakY - (baseWidth * sin(perpAngle)).toFloat()

                moveTo(tipX, tipY)
                lineTo(leftX, leftY)
                lineTo(rightX, rightY)
                close()
            }

            drawPath(path = trianglePath, color = Color.White.copy(alpha = 0.4f), blendMode = BlendMode.Screen)
            drawPath(path = trianglePath, color = Color.White)
        }

        // PEAK label above the triangle marker
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 38.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PEAK",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${peakDb.toInt()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NeonColors.NeonGreen,
                letterSpacing = 0.5.sp
            )
        }
    }
}
