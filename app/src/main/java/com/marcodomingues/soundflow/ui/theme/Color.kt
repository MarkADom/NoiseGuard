package com.marcodomingues.soundflow.ui.theme

import androidx.compose.ui.graphics.Color

// Dynamic Gradient Palette (NoiseCategory)
object NoiseColorPalette {
    // QUIET: Soft green → Bright green
    val quietGradient = listOf(
        Color(0xFFE8F5E9),
        Color(0xFF66BB6A)
    )

    // MODERATE: Light yellow → Orange
    val moderateGradient = listOf(
        Color(0xFFFFF9C4),
        Color(0xFFFFB74D)
    )

    // LOUD: Peach → Dark Orange
    val loudGradient = listOf(
        Color(0xFFFFE0B2),
        Color(0xFFFF6F00)
    )

    // HARMFUL: Pink → Red
    val harmfulGradient = listOf(
        Color(0xFFFFCDD2),
        Color(0xFFE53935)
    )

    // Base Colors Light Mode
    val LightPrimary = Color(0xFF1976D2)
    val LightBackground = Color(0xFFFAFAFA)
    val LightSurface = Color(0xFFFFFFFF)
    val LightOnBackground = Color(0xFF212121)

    //Base Colors Dark Mode
    val DarkPrimary = Color(0xFF64B5F6)
    val DarkBackground = Color(0xFF121212)
    val DarkSurface = Color(0xFF1E1E1E)
    val DarkOnBackground = Color(0xFFE0E0E0)
}