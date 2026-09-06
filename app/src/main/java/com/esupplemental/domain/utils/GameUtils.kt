package com.esupplemental.domain.utils

import androidx.compose.ui.graphics.Color
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.presentation.ui.theme.BrandNavyContainer
import com.esupplemental.presentation.ui.theme.BrandNavy
import com.esupplemental.presentation.ui.theme.BrandNavyDark
import com.esupplemental.presentation.ui.theme.BrandAquaContainer
import com.esupplemental.presentation.ui.theme.BrandAqua
import com.esupplemental.presentation.ui.theme.BrandTealDark
import com.esupplemental.presentation.ui.theme.BrandTealContainer
import com.esupplemental.presentation.ui.theme.BrandTeal

/**
 * Utility extension to map game difficulty to the design system's semantic colors.
 */
object GameUtils {
    val GameDifficulty.primaryColor: Color
        get() = when (this) {
            GameDifficulty.EASY     -> BrandTeal
            GameDifficulty.MODERATE -> BrandAqua
            GameDifficulty.HARD     -> BrandNavy
        }

    val GameDifficulty.darkColor: Color
        get() = when (this) {
            GameDifficulty.EASY     -> BrandTealDark
            GameDifficulty.MODERATE -> BrandTealDark
            GameDifficulty.HARD     -> BrandNavyDark
        }

    val GameDifficulty.lightColor: Color
        get() = when (this) {
            GameDifficulty.EASY     -> BrandTealContainer
            GameDifficulty.MODERATE -> BrandAquaContainer
            GameDifficulty.HARD     -> BrandNavyContainer
        }

    val GameDifficulty.onPrimaryColor: Color
        get() = if (this == GameDifficulty.MODERATE) BrandNavyDark else Color.White
}
