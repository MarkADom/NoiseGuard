package com.marcodomingues.noiseguard.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcodomingues.noiseguard.ui.theme.NeonColors

/**
 * LED-style dB number with a cyan glow, designed to overlay the CircularGaugeMeter centre.
 * Uses fade (not slide) so the large number doesn't feel jumpy at 10Hz updates.
 */
@Composable
fun DigitalDisplay(
    value: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .offset(y = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith
                        fadeOut(animationSpec = tween(200))
            },
            label = "digital_display"
        ) { targetValue ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                // Glow layer drawn behind the number
                Canvas(
                    modifier = Modifier
                        .width(130.dp)
                        .height(85.dp)
                ) {
                    drawCircle(
                        color = NeonColors.CyanStart.copy(alpha = 0.25f),
                        radius = 85f,
                        center = Offset(size.width / 2, size.height / 2),
                        blendMode = BlendMode.Screen
                    )
                }

                Text(
                    text = String.format("%02d", targetValue),
                    fontSize = 85.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color(0xFFE0FFFF),
                    textAlign = TextAlign.Center,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = NeonColors.CyanStart,
                            offset = Offset(0f, 0f),
                            blurRadius = 30f
                        )
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "dB",
            fontSize = 18.sp,
            fontWeight = FontWeight.Light,
            color = Color.White.copy(alpha = 0.5f),
            letterSpacing = 4.sp
        )
    }
}

/**
 * Shows the current noise category name and live dB value with a pulsing indicator dot.
 */
@Composable
fun CurrentLevelIndicator(
    value: Int,
    category: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = category.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = when (category.uppercase()) {
                "QUIET" -> Color(0xFF66BB6A)
                "MODERATE" -> Color(0xFFFFB74D)
                "LOUD" -> Color(0xFFFF6F00)
                "HARMFUL" -> Color(0xFFE53935)
                else -> Color.White
            },
            letterSpacing = 2.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CURRENT LEVEL:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            Text(
                text = "$value dB",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Canvas(modifier = Modifier.size(8.dp)) {
                // Outer glow
                drawCircle(
                    color = NeonColors.NeonGreen.copy(alpha = 0.4f),
                    radius = size.width * 1.5f
                )
                // Solid inner dot
                drawCircle(
                    color = NeonColors.NeonGreen,
                    radius = size.width / 2
                )
            }
        }
    }
}
