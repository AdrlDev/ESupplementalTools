package com.esupplemental.presentation.ui.screens.game

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.PlayfulShapes
import com.esupplemental.presentation.ui.theme.spacing

/**
 * A theme-aware "Glass" surface for game UI elements.
 * Uses Material 3 tonal elevation and surface variants to adapt to light/dark modes.
 */
@Composable
fun GameGlassSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        shape = PlayfulShapes.GameCard,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = MaterialTheme.spacing.extraSmall,
        modifier = modifier
    ) {
        content()
    }
}

/**
 * A colorful chip for displaying skills or game metadata.
 */
@Composable
fun GameChip(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = ArcadeColors.Teal
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = color.copy(alpha = 0.2f),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.extraSmall
            )
        )
    }
}
