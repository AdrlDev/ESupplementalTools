package com.esupplemental.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// Official E-Supplemental brand colors.
val BrandNavy = Color(0xFF0B2E63)
val BrandTeal = Color(0xFF0797A5)
val BrandAqua = Color(0xFF18C3C8)
val BrandBackground = Color(0xFFF8FCFD)
val BrandText = Color(0xFF102A43)

// Accessible supporting tones derived from the official palette.
val BrandNavyDark = Color(0xFF061D42)
val BrandNavyContainer = Color(0xFFDCE9F8)
val BrandNavyContainerText = Color(0xFF071D3D)
val BrandTealDark = Color(0xFF006974)
val BrandTealContainer = Color(0xFFC8F1F3)
val BrandTealContainerText = Color(0xFF073B42)
val BrandAquaContainer = Color(0xFFD2F7F7)
val BrandAquaContainerText = Color(0xFF003C3E)

// Light surfaces.
val BrandSurface = Color(0xFFFFFFFF)
val BrandSurfaceVariant = Color(0xFFE6F1F3)
val BrandOnSurfaceVariant = Color(0xFF425E68)
val BrandOutline = Color(0xFF6B8189)
val BrandOutlineVariant = Color(0xFFC3D4D8)

// Dark surfaces retain the navy identity without sacrificing contrast.
val BrandDarkBackground = Color(0xFF071624)
val BrandDarkSurface = Color(0xFF0D2235)
val BrandDarkSurfaceVariant = Color(0xFF193747)
val BrandDarkText = Color(0xFFE5F1F4)
val BrandDarkOnSurfaceVariant = Color(0xFFBDD0D5)
val BrandDarkOutline = Color(0xFF879BA1)
val BrandDarkOutlineVariant = Color(0xFF344E59)
val BrandDarkPrimary = Color(0xFFA8CAFA)
val BrandDarkSecondary = Color(0xFF68DDE1)
val BrandDarkTertiary = Color(0xFF7BE5E5)
val BrandDarkOnPrimaryContainer = Color(0xFFD9E7FA)
val BrandDarkOnSecondary = Color(0xFF00363B)
val BrandDarkOnSecondaryContainer = Color(0xFFB9F2F4)
val BrandDarkOnTertiary = Color(0xFF003738)
val BrandDarkTertiaryContainer = Color(0xFF00696D)
val BrandDarkOnTertiaryContainer = Color(0xFFB7F5F5)

// Semantic feedback and reward colors.
val ErrorRed = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF410002)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)
val SuccessGreen = Color(0xFF1F7A50)
val WarningOrange = Color(0xFFD97706)
val RewardGold = Color(0xFFE3A008)
val StarGold = RewardGold

// Readable semantic foregrounds for text and icons on light/dark surfaces.
val SuccessGreenDark = Color(0xFF70D6A0)
val WarningTextLight = Color(0xFF8A4B00)
val WarningTextDark = Color(0xFFFFB86C)
val RewardTextLight = Color(0xFF765A00)
val RewardTextDark = Color(0xFFFFD166)

/**
 * Playful game accents constrained to brand and semantic roles.
 * Names describe purpose so games do not introduce unrelated palettes.
 */
object ArcadeColors {
    val Navy = BrandNavy
    val Teal = BrandTeal
    val Aqua = BrandAqua
    val Reward = RewardGold
    val Success = SuccessGreen
    val Warning = WarningOrange
    val Purple = Color(0xFF7C3AED)
    val Emerald = SuccessGreen
    val Rose = ErrorRed
}
