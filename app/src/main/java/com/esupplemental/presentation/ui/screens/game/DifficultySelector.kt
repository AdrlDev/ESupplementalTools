package com.esupplemental.presentation.ui.screens.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun DifficultySelector(
    selected: GameDifficulty,
    onSelect: (GameDifficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GameDifficulty.entries.forEach { difficulty ->
            DifficultyTab(
                difficulty = difficulty,
                isSelected = difficulty == selected,
                onClick = { onSelect(difficulty) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DifficultyTab(
    difficulty: GameDifficulty,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val difficultyColors = rememberGameDifficultyColors(difficulty)
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tab_scale_${difficulty.name}"
    )

    val baseTextColor = MaterialTheme.colorScheme.onSurface

    // Translucent Glass Fill Colors
    val glassBgTop by animateColorAsState(
        targetValue = if (isSelected) {
            difficultyColors.container.copy(alpha = 0.82f)
        } else {
            baseTextColor.copy(alpha = 0.04f)
        },
        animationSpec = tween(220),
        label = "tab_bg_top_${difficulty.name}"
    )

    val glassBgBottom by animateColorAsState(
        targetValue = if (isSelected) {
            difficultyColors.container.copy(alpha = 0.48f)
        } else {
            baseTextColor.copy(alpha = 0.01f)
        },
        animationSpec = tween(220),
        label = "tab_bg_bottom_${difficulty.name}"
    )

    // Frosted Border Highlights
    val glassBorderTop by animateColorAsState(
        targetValue = if (isSelected) {
            difficultyColors.accent.copy(alpha = 0.90f)
        } else {
            baseTextColor.copy(alpha = 0.12f)
        },
        animationSpec = tween(220),
        label = "tab_border_top_${difficulty.name}"
    )

    val glassBorderBottom by animateColorAsState(
        targetValue = if (isSelected) {
            difficultyColors.accent.copy(alpha = 0.30f)
        } else {
            baseTextColor.copy(alpha = 0.03f)
        },
        animationSpec = tween(220),
        label = "tab_border_bottom_${difficulty.name}"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            difficultyColors.onContainer
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
        },
        animationSpec = tween(220),
        label = "tab_content_${difficulty.name}"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            // Frosted Glass Gradient Background
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(glassBgTop, glassBgBottom)
                )
            )
            // Glowing Glass Border (Thicker when selected for highlight)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(glassBorderTop, glassBorderBottom)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = difficulty.emoji,
                contentDescription = difficulty.label,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = difficulty.label,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
            Text(
                text = when (difficulty) {
                    GameDifficulty.EASY -> "Warm-up"
                    GameDifficulty.MODERATE -> "Level up"
                    GameDifficulty.HARD -> "Expert"
                },
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.78f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Difficulty Selector - Glassmorphism")
@Composable
fun DifficultySelectorPreview() {
    val selectedDifficulty = remember { mutableStateOf(GameDifficulty.EASY) }

    ESupplementalTheme {
        AppBackground {
            Box(modifier = Modifier.padding(24.dp)) {
                DifficultySelector(
                    selected = selectedDifficulty.value,
                    onSelect = { selectedDifficulty.value = it }
                )
            }
        }
    }
}
