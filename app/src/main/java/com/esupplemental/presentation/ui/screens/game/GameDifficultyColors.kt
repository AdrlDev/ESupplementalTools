package com.esupplemental.presentation.ui.screens.game

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.esupplemental.domain.model.game.GameDifficulty

/** Material color roles for a difficulty that remain readable in both themes. */
@Immutable
data class GameDifficultyColors(
    val accent: Color,
    val onAccent: Color,
    val container: Color,
    val onContainer: Color
)

@Composable
fun rememberGameDifficultyColors(difficulty: GameDifficulty): GameDifficultyColors {
    val scheme = MaterialTheme.colorScheme
    return remember(difficulty, scheme) {
        when (difficulty) {
            GameDifficulty.EASY -> GameDifficultyColors(
                accent = scheme.secondary,
                onAccent = scheme.onSecondary,
                container = scheme.secondaryContainer,
                onContainer = scheme.onSecondaryContainer
            )
            GameDifficulty.MODERATE -> GameDifficultyColors(
                accent = scheme.tertiary,
                onAccent = scheme.onTertiary,
                container = scheme.tertiaryContainer,
                onContainer = scheme.onTertiaryContainer
            )
            GameDifficulty.HARD -> GameDifficultyColors(
                accent = scheme.primary,
                onAccent = scheme.onPrimary,
                container = scheme.primaryContainer,
                onContainer = scheme.onPrimaryContainer
            )
        }
    }
}
