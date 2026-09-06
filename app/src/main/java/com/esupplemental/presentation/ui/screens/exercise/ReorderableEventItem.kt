package com.esupplemental.presentation.ui.screens.exercise

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.*

// ── Reorderable Event Item ─────────────────────────────────────────────────

/**
 * A drag-free reorder card for story sequencing — Grade 6 "Classroom Arcade" style.
 * Respects Material 3 design tokens and typography rules.
 */
@Composable
fun ReorderableEventItem(
    orderNumber: Int,
    description: String,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    isSubmitted: Boolean,
    isInteractionEnabled: Boolean = true,
    isCorrect: Boolean,
    isLoadingAudio: Boolean = false,
    onPlayAudio: () -> Unit = {},
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes

    val cardBorder by animateColorAsState(
        targetValue = when {
            isSubmitted && isCorrect -> SuccessGreen
            isSubmitted && !isCorrect -> colorScheme.outline.copy(alpha = 0.30f)
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "card_border"
    )
    val cardBg by animateColorAsState(
        targetValue = when {
            isSubmitted && isCorrect -> SuccessGreen.copy(alpha = 0.05f)
            else -> colorScheme.surface
        },
        animationSpec = tween(300),
        label = "card_bg"
    )

    val resultScale by animateFloatAsState(
        targetValue = if (isSubmitted && isCorrect) 1f else 0.98f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "result_scale"
    )

    Box(
        modifier = Modifier
            .scale(resultScale)
            .fillMaxWidth()
            .clip(shapes.medium)
            .border(2.dp, cardBorder, shapes.medium)
            .background(cardBg)
    ) {
        Row(
            modifier = Modifier.padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            // ── Number badge ───────────────────────────────────────────────
            val badgeGradient = if (isSubmitted && isCorrect) {
                Brush.linearGradient(
                    listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.65f)),
                    start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            } else {
                Brush.linearGradient(
                    listOf(colorScheme.secondary, colorScheme.secondaryContainer),
                    start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(badgeGradient)
            ) {
                Text(
                    text = "$orderNumber",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color.White
                )
            }

            // ── Event description ──────────────────────────────────────────
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            // ── Sound Icon ──
            if (isLoadingAudio) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = colorScheme.primary
                )
            } else {
                IconButton(onClick = onPlayAudio, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Play Event Audio",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ── Controls or result icon ────────────────────────────────────
            if (!isSubmitted) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ArrowButton(
                        icon = Icons.Default.KeyboardArrowUp,
                        label = "Move up",
                        enabled = isInteractionEnabled && canMoveUp,
                        color = colorScheme.secondary,
                        onClick = onMoveUp
                    )
                    ArrowButton(
                        icon = Icons.Default.KeyboardArrowDown,
                        label = "Move down",
                        enabled = isInteractionEnabled && canMoveDown,
                        color = colorScheme.secondary,
                        onClick = onMoveDown
                    )
                }
            } else {
                // Result badge
                val iconScale by animateFloatAsState(
                    targetValue = 1.2f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
                    label = "icon_scale"
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .scale(iconScale)
                        .clip(CircleShape)
                        .background(
                            if (isCorrect) SuccessGreen.copy(alpha = 0.15f)
                            else colorScheme.surfaceVariant
                        )
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.SwapVert,
                        contentDescription = null,
                        tint = if (isCorrect) SuccessGreen
                        else colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// ── Arrow button helper ────────────────────────────────────────────────────

@Composable
private fun ArrowButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes

    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.85f,
        animationSpec = tween(150),
        label = "arrow_scale"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .scale(scale)
            .size(30.dp)
            .clip(shapes.extraSmall)
            .background(
                if (enabled) color.copy(alpha = 0.12f)
                else colorScheme.surfaceVariant.copy(alpha = 0.50f)
            )
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) color
                else colorScheme.onSurface.copy(alpha = 0.30f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


// ── Previews ──────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Reorderable Event – Light")
@Composable
private fun ReorderableEventItemLightPreview() {
    ESupplementalTheme(darkTheme = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReorderableEventItem(
                orderNumber = 1,
                description = "Alex wakes up and hears a strange noise downstairs.",
                canMoveUp = false,
                canMoveDown = true,
                isSubmitted = false,
                isCorrect = false,
                onMoveUp = {},
                onMoveDown = {}
            )
        }
    }
}
