package com.esupplemental.presentation.ui.screens.exercise

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.*

/**
 * A vibrant, Grade-6-friendly section header.
 * Standardized to follow M3 design tokens.
 */
@Composable
fun ExerciseSectionHeader(
    title: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.primary,
    badgeSecondaryColor: Color = color.copy(alpha = 0.55f)
) {
    val spacing = MaterialTheme.spacing
    val shapes = MaterialTheme.shapes

    val infiniteTransition = rememberInfiniteTransition(label = "header_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6_000, easing = LinearEasing)
        ),
        label = "dot_rotation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icon Badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(shapes.extraLarge) // Circle-like roundness
                .background(
                    Brush.linearGradient(
                        colors = listOf(color, badgeSecondaryColor)
                    )
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        // Title pill
        Box(
            modifier = Modifier
                .clip(shapes.extraLarge)
                .background(color.copy(alpha = 0.10f))
                .padding(horizontal = spacing.medium, vertical = spacing.extraSmall)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = color
            )
        }

        Spacer(Modifier.weight(1f))

        // Decorative spinning dots
        Box(
            modifier = Modifier
                .size(28.dp)
                .rotate(angle),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .offset(x = 7.dp, y = (-7).dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.45f))
            )
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .offset(x = (-6).dp, y = 6.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.25f))
            )
        }
    }
}

@Preview(showBackground = true, name = "Exercise Headers – Light")
@Composable
private fun ExerciseSectionHeaderLightPreview() {
    ESupplementalTheme(darkTheme = false) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ExerciseSectionHeader(
                title = "Fill in the Blanks",
                icon = Icons.Rounded.MusicNote,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
