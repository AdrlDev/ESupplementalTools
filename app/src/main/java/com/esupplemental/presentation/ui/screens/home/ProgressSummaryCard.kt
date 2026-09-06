package com.esupplemental.presentation.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.components.CircularProgressRing
import androidx.compose.ui.tooling.preview.Preview
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing

/**
 * A summary card showing the user's level and progress.
 * Adheres to M3 colors and typography (Quicksand for numbers).
 */
@Composable
fun ProgressSummaryCard(level: Int, levelProgress: Float) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            colorScheme.primaryContainer,
            colorScheme.secondaryContainer
        )
    )

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.small),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(cardGradient)
                .padding(spacing.large)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = colorScheme.secondary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "CURRENT STATUS",
                            modifier = Modifier.padding(
                                horizontal = spacing.small, 
                                vertical = spacing.extraSmall
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.secondary
                        )
                    }

                    Spacer(Modifier.height(spacing.medium))

                    Text(
                        text = "Keep it up!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = colorScheme.onPrimaryContainer
                    )

                    Spacer(Modifier.height(spacing.extraSmall))

                    // Text with numbers uses titleLarge (Quicksand) or bodyMedium
                    Text(
                        text = "Level $level · ${(levelProgress * 100).toInt()}% complete",
                        style = MaterialTheme.typography.titleSmall,
                        color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Spacer(Modifier.height(spacing.medium))

                    LinearProgressIndicator(
                        progress = { levelProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spacing.small)
                            .clip(CircleShape),
                        color = colorScheme.secondary,
                        trackColor = colorScheme.surface.copy(alpha = 0.5f)
                    )
                }

                Spacer(Modifier.width(spacing.medium))

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressRing(
                        progress = levelProgress,
                        size = 95.dp,
                        strokeWidth = 12.dp,
                        progressColor = colorScheme.secondary,
                        trackColor = colorScheme.surface.copy(alpha = 0.3f),
                        label = "Lv$level",
                        sublabel = "${(levelProgress * 100).toInt()}%"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Progress Card - Mid Level")
@Composable
fun ProgressSummaryCardPreview() {
    ESupplementalTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProgressSummaryCard(level = 5, levelProgress = 0.65f)
        }
    }
}
