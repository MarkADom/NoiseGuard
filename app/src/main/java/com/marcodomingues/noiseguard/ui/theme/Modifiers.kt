package com.marcodomingues.noiseguard.ui.theme

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

fun Modifier.noiseGuardBackground(): Modifier = this.background(
    brush = Brush.verticalGradient(
        colors = listOf(
            DarkPremium.BackgroundGradientStart,
            DarkPremium.BackgroundGradientEnd
        )
    )
)
