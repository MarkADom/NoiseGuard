package com.marcodomingues.noiseguard.ui.theme

import androidx.compose.ui.graphics.Color

// Neon Gradient Colors
object NeonColors {
    val CyanStart = Color(0xFF00E5FF)
    val CyanMid = Color(0xFF00B8D4)
    val MagentaMid = Color(0xFFE040FB)
    val MagentaEnd = Color(0xFFFF00E5)

    val NeonGreen = Color(0xFF00FF94)
    val NeonBlue = Color(0xFF00D9FF)
    val NeonPurple = Color(0xFFBB00FF)

    val OnSurfaceVariant = Color(0xFF9AA0A6)

    // Gradient presets
    val gaugeGradient = listOf(CyanStart, CyanMid, MagentaMid, MagentaEnd)
}

// Dark Premium Theme
object DarkPremium {
    val Background = Color(0xFF0A0E1A)
    val BackgroundGradientStart = Color(0xFF0A0E1A)
    val BackgroundGradientEnd = Color(0xFF1A1A2E)

    val Surface = Color(0xFF151829)
    val SurfaceVariant = Color(0xFF1E2235)

    val OnBackground = Color(0xFFE8EAED)
    val OnSurfaceVariant = Color(0xFF9AA0A6)
    val Destructive = Color(0xFFE53935)
}

// Noise Category Gradients
object NoiseColorPalette {
    val quietGradient = listOf(Color(0xFFE8F5E9), Color(0xFF66BB6A))
    val moderateGradient = listOf(Color(0xFFFFF9C4), Color(0xFFFFB74D))
    val loudGradient = listOf(Color(0xFFFFE0B2), Color(0xFFFF6F00))
    val harmfulGradient = listOf(Color(0xFFFFCDD2), Color(0xFFE53935))
}