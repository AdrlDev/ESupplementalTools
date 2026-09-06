package com.esupplemental.presentation.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized spacing system for the application.
 * Follows an 8dp grid system.
 */
data class Spacing(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 48.dp,
    val screenPadding: Dp = 20.dp
)

/**
 * Centralized elevation system for the application.
 */
data class Elevations(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 2.dp,
    val small: Dp = 4.dp,
    val medium: Dp = 8.dp,
    val large: Dp = 12.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
val LocalElevations = staticCompositionLocalOf { Elevations() }
