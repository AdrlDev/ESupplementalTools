package com.esupplemental.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.elevations
import com.esupplemental.presentation.ui.theme.spacing

/**
 * A playful arcade-style card for navigating to major activities.
 */
@Composable
fun ActivityCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val shapes = MaterialTheme.shapes
    val elevations = MaterialTheme.elevations
    val resolvedContentColor = if (contentColor == Color.Unspecified) {
        contentColorFor(containerColor)
    } else {
        contentColor
    }

    Card(
        onClick = onClick,
        shape = shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevations.small),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = resolvedContentColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(containerColor, containerColor.copy(alpha = 0.85f))
                    )
                )
                .padding(spacing.medium)
        ) {
            // Subtle "Glow" effect
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = (-40).dp, y = (-40).dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                // Icon Sticker Container
                Surface(
                    shape = shapes.medium,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = resolvedContentColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.width(spacing.medium))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = resolvedContentColor
                    )
                    Spacer(Modifier.height(spacing.extraSmall))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = resolvedContentColor.copy(alpha = 0.9f)
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = resolvedContentColor.copy(alpha = 0.65f),
                    modifier = Modifier.size(spacing.extraLarge)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Activity Card - Light")
@Composable
fun ActivityCardPreview() {
    ESupplementalTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            ActivityCard(
                title = "Songs",
                description = "Listen to songs and fill in the blanks",
                icon = Icons.Rounded.MusicNote,
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {}
            )
        }
    }
}
