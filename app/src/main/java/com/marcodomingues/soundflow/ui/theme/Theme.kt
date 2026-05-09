package com.marcodomingues.soundflow.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.marcodomingues.soundflow.ui.theme.NoiseColorPalette.DarkBackground
import com.marcodomingues.soundflow.ui.theme.NoiseColorPalette.DarkOnBackground
import com.marcodomingues.soundflow.ui.theme.NoiseColorPalette.DarkPrimary
import com.marcodomingues.soundflow.ui.theme.NoiseColorPalette.DarkSurface
import com.marcodomingues.soundflow.ui.theme.NoiseColorPalette.LightBackground
import com.marcodomingues.soundflow.ui.theme.NoiseColorPalette.LightOnBackground
import com.marcodomingues.soundflow.ui.theme.NoiseColorPalette.LightPrimary
import com.marcodomingues.soundflow.ui.theme.NoiseColorPalette.LightSurface

/**
 * Light mode color scheme following Material 3 guidelines.
 * Uses fixed palette for brand consistency.
 */
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnBackground,
    onSurface = LightOnBackground
    // Note: secondary color uses Material 3 defaults
)

/**
 * Dark mode color scheme optimized for OLED displays.
 * StatusBar adapts automatically via SideEffect.
 */
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnBackground
)

/**
 * SoundFlow app theme with automatic dark mode support.
 * Configures StatusBar appearance based on theme.
 *
 * @param darkTheme Whether to use dark theme (follows system by default)
 * @param content Composable content to apply theme to
 */
@Composable
fun SoundFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Configure StatusBar appearance
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}