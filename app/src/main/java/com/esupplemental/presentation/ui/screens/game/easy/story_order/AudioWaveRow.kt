package com.esupplemental.presentation.ui.screens.game.easy.story_order

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing

@Composable
fun AudioWaveRow(infiniteTransition: InfiniteTransition) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme

    // Glassmorphism Base Colors
    val baseContentColor = colorScheme.onSurface
    val glassBgTop = baseContentColor.copy(alpha = 0.08f)
    val glassBgBottom = baseContentColor.copy(alpha = 0.02f)
    val glassBorderTop = baseContentColor.copy(alpha = 0.20f)
    val glassBorderBottom = baseContentColor.copy(alpha = 0.05f)

    val barCount = 7

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(glassBgTop, glassBgBottom)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(glassBorderTop, glassBorderBottom)
                ),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = spacing.large, vertical = spacing.medium),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(barCount) { i ->
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.25f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        tween(400, delayMillis = i * 65, easing = FastOutSlowInEasing),
                        RepeatMode.Reverse
                    ),
                    label = "bar_$i"
                )

                // Dynamic Alternating Gradient Bars
                val barBrush = if (i % 2 == 0) {
                    Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.secondary,
                            colorScheme.secondary.copy(alpha = 0.6f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.primary,
                            colorScheme.primary.copy(alpha = 0.6f)
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height((28 * scale + 8).dp)
                        .clip(CircleShape)
                        .background(barBrush)
                )
            }
        }
    }
}

@Preview(name = "Active Audio Wave")
@Composable
fun AudioWaveRowPreview() {
    val infiniteTransition = rememberInfiniteTransition(label = "preview_wave")

    ESupplementalTheme {
        AppBackground {
            Box(
                modifier = Modifier.padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                AudioWaveRow(infiniteTransition = infiniteTransition)
            }
        }
    }
}