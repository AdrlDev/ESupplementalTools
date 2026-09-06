package com.esupplemental.presentation.ui.screens.game.easy.story_order

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.data.model.game.StoryEvent
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.CardCheckState
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun EventCard(
    event: StoryEvent,
    position: Int,
    isDragging: Boolean,
    isDropTarget: Boolean,
    dragOffsetY: Float,
    checkState: CardCheckState,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val stepColors = listOf(
        ArcadeColors.Teal,
        ArcadeColors.Purple,
        ArcadeColors.Warning,
        ArcadeColors.Navy,
        ArcadeColors.Emerald
    )
    val stepAccent = stepColors[(position - 1).coerceIn(0, stepColors.lastIndex)]

    // Bouncy scale during drag
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else if (isDropTarget) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "drag_scale"
    )

    val cardBackground = when (checkState) {
        CardCheckState.CORRECT -> Brush.verticalGradient(
            listOf(
                ArcadeColors.Emerald.copy(alpha = 0.22f),
                ArcadeColors.Emerald.copy(alpha = 0.08f)
            )
        )
        CardCheckState.WRONG -> Brush.verticalGradient(
            listOf(
                ArcadeColors.Rose.copy(alpha = 0.22f),
                ArcadeColors.Rose.copy(alpha = 0.08f)
            )
        )
        CardCheckState.IDLE -> if (isDragging) {
            Brush.verticalGradient(
                listOf(
                    ArcadeColors.Teal.copy(alpha = 0.25f),
                    ArcadeColors.Teal.copy(alpha = 0.10f)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    colorScheme.surfaceVariant.copy(alpha = 0.25f)
                )
            )
        }
    }

    val targetBorderColor = when {
        checkState == CardCheckState.CORRECT -> ArcadeColors.Emerald
        checkState == CardCheckState.WRONG -> ArcadeColors.Rose
        isDropTarget -> ArcadeColors.Teal
        isDragging -> ArcadeColors.Teal
        else -> colorScheme.outline.copy(alpha = 0.25f)
    }

    val animatedBorderColor by animateColorAsState(targetBorderColor, tween(250), label = "card_border")

    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = if (isDragging || isDropTarget || checkState != CardCheckState.IDLE) 2.dp else 1.dp,
            color = animatedBorderColor
        ),
        modifier = modifier
            .graphicsLayer {
                translationY = dragOffsetY
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackground)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Sequence Number / Status Icon Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (checkState) {
                            CardCheckState.CORRECT -> ArcadeColors.Emerald
                            CardCheckState.WRONG -> ArcadeColors.Rose
                            CardCheckState.IDLE -> stepAccent
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (checkState) {
                    CardCheckState.CORRECT -> Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Correct",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    CardCheckState.WRONG -> Icon(
                        imageVector = Icons.Rounded.Cancel,
                        contentDescription = "Wrong",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    CardCheckState.IDLE -> Text(
                        text = "$position",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Event Description
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                fontWeight = if (checkState == CardCheckState.CORRECT) FontWeight.Bold else FontWeight.Medium,
                color = when (checkState) {
                    CardCheckState.CORRECT -> ArcadeColors.Emerald
                    CardCheckState.WRONG -> ArcadeColors.Rose
                    CardCheckState.IDLE -> colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )

            // Tactile Drag Indicator
            if (checkState == CardCheckState.IDLE) {
                Icon(
                    imageVector = Icons.Rounded.DragIndicator,
                    contentDescription = "Drag to reorder",
                    tint = if (isDragging) ArcadeColors.Teal else colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Event Card: States Gallery")
@Composable
fun EventCardGalleryPreview() {
    val mockEvents = listOf(
        StoryEvent("s1", "Wilbur was born as the runt of the litter.", 1),
        StoryEvent("s2", "Fern saved Wilbur from her father's axe.", 2),
        StoryEvent("s3", "Wilbur moved to Zuckerman's barn.", 3)
    )

    ESupplementalTheme {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EventCard(
                    event = mockEvents[0],
                    position = 1,
                    isDragging = false,
                    isDropTarget = false,
                    dragOffsetY = 0f,
                    checkState = CardCheckState.IDLE
                )

                EventCard(
                    event = mockEvents[1],
                    position = 2,
                    isDragging = false,
                    isDropTarget = false,
                    dragOffsetY = 0f,
                    checkState = CardCheckState.CORRECT
                )

                EventCard(
                    event = mockEvents[2],
                    position = 3,
                    isDragging = false,
                    isDropTarget = false,
                    dragOffsetY = 0f,
                    checkState = CardCheckState.WRONG
                )
            }
        }
    }
}