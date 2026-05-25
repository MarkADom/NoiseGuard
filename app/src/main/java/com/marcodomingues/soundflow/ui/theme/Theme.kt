package com.marcodomingues.noiseguard.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = NeonColors.CyanStart,
    secondary = NeonColors.MagentaEnd,
    tertiary = NeonColors.NeonGreen,
    background = DarkPremium.Background,
    surface = DarkPremium.Surface,
    surfaceVariant = DarkPremium.SurfaceVariant,
    onBackground = DarkPremium.OnBackground,
    onSurface = DarkPremium.OnBackground,
    onSurfaceVariant = DarkPremium.OnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = NeonColors.CyanMid,
    secondary = NeonColors.MagentaMid,
    tertiary = NeonColors.NeonGreen,
    background = DarkPremium.Background,
    surface = DarkPremium.Surface,
    onBackground = DarkPremium.OnBackground
)

@Composable
fun NoiseGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}