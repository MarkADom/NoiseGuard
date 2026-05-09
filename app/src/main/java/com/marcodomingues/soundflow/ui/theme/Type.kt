package com.marcodomingues.soundflow.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val Typography = Typography(
    // Decibel value (highlight)
    displayLarge = TextStyle(
        fontSize = 72.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-1.5).sp,
    ),

    // Category (secondary)
    titleMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.15.sp,
    ),

    // Statistics and labels
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp,
    ),

    // Small labels (timestamps, axes)
    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.4.sp
    )
)