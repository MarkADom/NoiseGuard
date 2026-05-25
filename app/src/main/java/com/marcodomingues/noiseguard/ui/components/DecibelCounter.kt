package com.marcodomingues.noiseguard.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

/**
 * Animated decibel counter with "odometer" style transitions.
 * Numbers slide vertically when changing.
 *
 * @param value Current decibel value to display
 * @param modifier Modifier for layout customization
 */
@Composable
fun DecibelCounter(
    value: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                slideInVertically { height -> height } togetherWith
                        slideOutVertically { height -> -height }
            },
            label = "decibel_counter"
        ) { targetValue ->
            Text(
                text = "$targetValue",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "dB",
            style = MaterialTheme.typography.titleMedium
        )
    }
}