package com.esupplemental.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BrandNavy,
    onPrimary = Color.White,
    primaryContainer = BrandNavyContainer,
    onPrimaryContainer = BrandNavyContainerText,
    inversePrimary = BrandDarkPrimary,

    secondary = BrandTeal,
    onSecondary = BrandNavyDark,
    secondaryContainer = BrandTealContainer,
    onSecondaryContainer = BrandTealContainerText,

    tertiary = BrandAqua,
    onTertiary = BrandAquaContainerText,
    tertiaryContainer = BrandAquaContainer,
    onTertiaryContainer = BrandAquaContainerText,

    background = BrandBackground,
    onBackground = BrandText,
    surface = BrandSurface,
    onSurface = BrandText,
    surfaceVariant = BrandSurfaceVariant,
    onSurfaceVariant = BrandOnSurfaceVariant,
    surfaceTint = BrandNavy,
    inverseSurface = BrandText,
    inverseOnSurface = BrandBackground,
    outline = BrandOutline,
    outlineVariant = BrandOutlineVariant,
    scrim = Color.Black,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandDarkPrimary,
    onPrimary = BrandNavyDark,
    primaryContainer = BrandNavy,
    onPrimaryContainer = BrandDarkOnPrimaryContainer,
    inversePrimary = BrandNavy,

    secondary = BrandDarkSecondary,
    onSecondary = BrandDarkOnSecondary,
    secondaryContainer = BrandTealDark,
    onSecondaryContainer = BrandDarkOnSecondaryContainer,

    tertiary = BrandDarkTertiary,
    onTertiary = BrandDarkOnTertiary,
    tertiaryContainer = BrandDarkTertiaryContainer,
    onTertiaryContainer = BrandDarkOnTertiaryContainer,

    background = BrandDarkBackground,
    onBackground = BrandDarkText,
    surface = BrandDarkSurface,
    onSurface = BrandDarkText,
    surfaceVariant = BrandDarkSurfaceVariant,
    onSurfaceVariant = BrandDarkOnSurfaceVariant,
    surfaceTint = BrandDarkPrimary,
    inverseSurface = BrandDarkText,
    inverseOnSurface = BrandNavyDark,
    outline = BrandDarkOutline,
    outlineVariant = BrandDarkOutlineVariant,
    scrim = Color.Black,

    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer
)

data class ThemeColorOption(
    val key: String,
    val name: String,
    val lightPrimary: Color,
    val lightPrimaryContainer: Color,
    val lightOnPrimaryContainer: Color,
    val darkPrimary: Color,
    val darkPrimaryContainer: Color,
    val darkOnPrimaryContainer: Color,
    val previewColor: Color
)

val ThemeColorPresets = listOf(
    ThemeColorOption(
        key = "navy",
        name = "Brand Navy",
        lightPrimary = BrandNavy,
        lightPrimaryContainer = BrandNavyContainer,
        lightOnPrimaryContainer = BrandNavyContainerText,
        darkPrimary = BrandDarkPrimary,
        darkPrimaryContainer = BrandNavy,
        darkOnPrimaryContainer = BrandDarkOnPrimaryContainer,
        previewColor = BrandNavy
    ),
    ThemeColorOption(
        key = "teal",
        name = "Ocean Teal",
        lightPrimary = BrandTeal,
        lightPrimaryContainer = BrandTealContainer,
        lightOnPrimaryContainer = BrandTealContainerText,
        darkPrimary = Color(0xFF68DDE1),
        darkPrimaryContainer = Color(0xFF004F56),
        darkOnPrimaryContainer = Color(0xFFC8F1F3),
        previewColor = BrandTeal
    ),
    ThemeColorOption(
        key = "purple",
        name = "Magic Purple",
        lightPrimary = Color(0xFF6B21A8),
        lightPrimaryContainer = Color(0xFFF3E8FF),
        lightOnPrimaryContainer = Color(0xFF3B0764),
        darkPrimary = Color(0xFFD8B4FE),
        darkPrimaryContainer = Color(0xFF581C87),
        darkOnPrimaryContainer = Color(0xFFF3E8FF),
        previewColor = Color(0xFF7C3AED)
    ),
    ThemeColorOption(
        key = "emerald",
        name = "Forest Green",
        lightPrimary = Color(0xFF047857),
        lightPrimaryContainer = Color(0xFFD1FAE5),
        lightOnPrimaryContainer = Color(0xFF064E3B),
        darkPrimary = Color(0xFF6EE7B7),
        darkPrimaryContainer = Color(0xFF065F46),
        darkOnPrimaryContainer = Color(0xFFD1FAE5),
        previewColor = Color(0xFF10B981)
    ),
    ThemeColorOption(
        key = "rose",
        name = "Berry Rose",
        lightPrimary = Color(0xFFBE123C),
        lightPrimaryContainer = Color(0xFFFFE4E6),
        lightOnPrimaryContainer = Color(0xFF881337),
        darkPrimary = Color(0xFFFDA4AF),
        darkPrimaryContainer = Color(0xFF9F1239),
        darkOnPrimaryContainer = Color(0xFFFFE4E6),
        previewColor = Color(0xFFF43F5E)
    ),
    ThemeColorOption(
        key = "gold",
        name = "Amber Gold",
        lightPrimary = Color(0xFFB45309),
        lightPrimaryContainer = Color(0xFFFEF3C7),
        lightOnPrimaryContainer = Color(0xFF78350F),
        darkPrimary = Color(0xFFFCD34D),
        darkPrimaryContainer = Color(0xFF92400E),
        darkOnPrimaryContainer = Color(0xFFFEF3C7),
        previewColor = Color(0xFFF59E0B)
    )
)

private fun getLightColorScheme(themeColorKey: String): androidx.compose.material3.ColorScheme {
    val preset = ThemeColorPresets.find { it.key == themeColorKey } ?: ThemeColorPresets.first()
    return LightColorScheme.copy(
        primary = preset.lightPrimary,
        primaryContainer = preset.lightPrimaryContainer,
        onPrimaryContainer = preset.lightOnPrimaryContainer,
        surfaceTint = preset.lightPrimary
    )
}

private fun getDarkColorScheme(themeColorKey: String): androidx.compose.material3.ColorScheme {
    val preset = ThemeColorPresets.find { it.key == themeColorKey } ?: ThemeColorPresets.first()
    return DarkColorScheme.copy(
        primary = preset.darkPrimary,
        primaryContainer = preset.darkPrimaryContainer,
        onPrimaryContainer = preset.darkOnPrimaryContainer,
        surfaceTint = preset.darkPrimary
    )
}

@Composable
fun ESupplementalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColorKey: String = "navy",
    @Suppress("UNUSED_PARAMETER") dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Brand colors intentionally take precedence over Android dynamic color.
    val colorScheme = if (darkTheme) getDarkColorScheme(themeColorKey) else getLightColorScheme(themeColorKey)
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) colorScheme.surface.toArgb() else colorScheme.primary.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalElevations provides Elevations()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

val MaterialTheme.spacing: Spacing
    @Composable get() = LocalSpacing.current

val MaterialTheme.elevations: Elevations
    @Composable get() = LocalElevations.current
