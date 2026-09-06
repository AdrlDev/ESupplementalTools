package com.esupplemental.presentation.ui.screens.exercise

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
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

// ── Choice Option ─────────────────────────────────────────────────────────

/** Labels shown on the left-side badge: A, B, C, D, … */
private val OPTION_LABELS = listOf("A", "B", "C", "D", "E", "F")

/**
 * A single multiple-choice option styled for Grade 6 students.
 * Respects Material 3 design tokens and typography rules.
 */
@Composable
fun ChoiceOption(
    text: String,
    selected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    isSubmitted: Boolean,
    onClick: () -> Unit,
    optionIndex: Int = 0,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes
    
    val label = OPTION_LABELS.getOrElse(optionIndex) { "?" }

    val rowBackground by animateColorAsState(
        targetValue = when {
            isCorrect -> SuccessGreen.copy(alpha = 0.12f)
            isWrong -> colorScheme.error.copy(alpha = 0.10f)
            selected -> accentColor.copy(alpha = 0.10f)
            else -> colorScheme.surface
        },
        animationSpec = tween(250),
        label = "row_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isCorrect -> SuccessGreen
            isWrong -> colorScheme.error
            selected -> accentColor
            else -> colorScheme.outline.copy(alpha = 0.45f)
        },
        animationSpec = tween(250),
        label = "border"
    )

    val rowScale by animateFloatAsState(
        targetValue = if ((isCorrect || isWrong) && isSubmitted) 1f else if (selected) 1.015f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "row_scale"
    )

    Row(
        modifier = Modifier
            .scale(rowScale)
            .fillMaxWidth()
            .clip(shapes.medium)
            .background(rowBackground)
            .border(1.dp, borderColor, shapes.medium)
            .clickable(enabled = !isSubmitted, onClick = onClick)
            .padding(horizontal = spacing.medium, vertical = spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        // ── Letter badge ───────────────────────────────────────────────────
        val badgeGradient = when {
            isCorrect -> Brush.linearGradient(
                listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.7f)),
                start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY)
            )

            isWrong -> Brush.linearGradient(
                listOf(colorScheme.error, colorScheme.error.copy(alpha = 0.7f)),
                start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY)
            )

            selected -> Brush.linearGradient(
                listOf(accentColor, accentColor.copy(alpha = 0.70f)),
                start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY)
            )

            else -> Brush.linearGradient(
                listOf(
                    colorScheme.surfaceVariant,
                    colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ),
                start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY)
            )
        }
        val badgeScale by animateFloatAsState(
            targetValue = if (selected || isCorrect || isWrong) 1.15f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "badge_scale"
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .scale(badgeScale)
                .size(36.dp)
                .clip(CircleShape)
                .background(badgeGradient)
        ) {
            Text(
                text = label,
                color = if (selected || isCorrect || isWrong) Color.White
                else colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        // ── Option text ────────────────────────────────────────────────────
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (selected || isCorrect || isWrong) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = when {
                isCorrect -> SuccessGreen
                isWrong -> colorScheme.error
                selected -> accentColor
                else -> colorScheme.onSurface
            },
            modifier = Modifier.weight(1f)
        )

        // ── Result icon ────────────────────────────────────────────────────
        if (isSubmitted && (isCorrect || isWrong)) {
            val iconScale by animateFloatAsState(
                targetValue = 1.3f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
                label = "result_icon_scale"
            )
            Icon(
                imageVector = if (isCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                contentDescription = null,
                tint = if (isCorrect) SuccessGreen else colorScheme.error,
                modifier = Modifier
                    .size(26.dp)
                    .scale(iconScale)
            )
        }
    }
}


// ── Previews ──────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Choice Options – All States Light")
@Composable
private fun ChoiceOptionLightPreview() {
    ESupplementalTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ChoiceOption(
                text = "Hypertext Markup Language",
                selected = false,
                isCorrect = false,
                isWrong = false,
                isSubmitted = false,
                optionIndex = 0,
                onClick = {}
            )
            ChoiceOption(
                text = "Hypertext Markup Language (Correct)",
                selected = true,
                isCorrect = true,
                isWrong = false,
                isSubmitted = true,
                optionIndex = 2,
                onClick = {}
            )
        }
    }
}
