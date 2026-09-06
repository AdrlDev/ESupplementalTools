package com.esupplemental.presentation.ui.screens.exercise

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.presentation.ui.theme.*

// ── Fill Blank Item ────────────────────────────────────────────────────────

/**
 * A single fill-in-the-blank card, redesigned with a "classroom arcade" look.
 * Respects Material 3 design tokens and typography rules.
 */
@Composable
fun FillBlankItem(
    sentence: String,
    answer: String,
    isSubmitted: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    correctAnswer: String,
    onAnswerChange: (String) -> Unit,
    itemNumber: Int = 1,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes

    val resultScale by animateFloatAsState(
        targetValue = if (isSubmitted) 1f else 0.96f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    val cardBorderColor by animateColorAsState(
        targetValue = when {
            isCorrect -> SuccessGreen
            isWrong -> colorScheme.error
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "border_color"
    )

    val cardBackground = when {
        isCorrect -> SuccessGreen.copy(alpha = 0.09f)
        isWrong -> colorScheme.error.copy(alpha = 0.08f)
        else -> colorScheme.surface
    }

    Box(
        modifier = Modifier
            .scale(resultScale)
            .fillMaxWidth()
            .clip(shapes.medium)
            .border(2.dp, cardBorderColor, shapes.medium)
            .background(cardBackground)
    ) {
        Column {
            // ── Coloured top accent strip ──────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        Brush.linearGradient(
                            colors = when {
                                isCorrect -> listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.65f))
                                isWrong -> listOf(colorScheme.error, colorScheme.error.copy(alpha = 0.65f))
                                else -> listOf(accentColor, accentColor.copy(alpha = 0.60f))
                            },
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, 0f)
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Numbered badge - Uses Quicksand via labelLarge
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.30f))
                    ) {
                        Text(
                            text = "$itemNumber",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }

                    // Label text
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(spacing.extraSmall))
                        Text(
                            text = "Fill in the blank",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            // ── Card body ─────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = spacing.medium, vertical = spacing.medium)) {

                // Sentence
                Text(
                    text = buildAnnotatedString {
                        val parts = sentence.split("___")
                        parts.forEachIndexed { i, part ->
                            append(part)
                            if (i < parts.size - 1) {
                                withStyle(
                                    SpanStyle(
                                        color = accentColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        fontStyle = FontStyle.Italic
                                    )
                                ) { append(" _______ ") }
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    )
                )

                Spacer(Modifier.height(spacing.medium))

                // Answer field
                OutlinedTextField(
                    value = answer,
                    onValueChange = onAnswerChange,
                    enabled = !isSubmitted,
                    placeholder = {
                        Text(
                            "Type your answer here...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        )
                    },
                    shape = shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (isSubmitted) {
                            val iconScale by animateFloatAsState(
                                targetValue = 1.25f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
                                label = "icon_scale"
                            )
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (isCorrect) SuccessGreen else colorScheme.error,
                                modifier = Modifier
                                    .size(26.dp)
                                    .scale(iconScale)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = accentColor.copy(alpha = 0.35f),
                        disabledBorderColor = when {
                            isCorrect -> SuccessGreen.copy(alpha = 0.6f)
                            isWrong -> colorScheme.error.copy(alpha = 0.6f)
                            else -> colorScheme.outline.copy(alpha = 0.4f)
                        }
                    )
                )

                // Wrong-answer hint bubble
                if (isWrong) {
                    Spacer(Modifier.height(spacing.small))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shapes.small)
                            .background(colorScheme.error.copy(alpha = 0.12f))
                            .padding(horizontal = spacing.medium, vertical = spacing.small),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = null,
                            tint = colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Correct answer: $correctAnswer",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.error
                            )
                        )
                    }
                }

                // Correct celebration hint
                if (isCorrect) {
                    Spacer(Modifier.height(spacing.small))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shapes.small)
                            .background(SuccessGreen.copy(alpha = 0.12f))
                            .padding(horizontal = spacing.medium, vertical = spacing.extraSmall),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Celebration,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Great job! That's correct!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SuccessGreen
                            )
                        )
                    }
                }
            }
        }
    }
}


// ── Previews ──────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Fill Blank States – Light")
@Composable
private fun FillBlankItemPreview() {
    ESupplementalTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FillBlankItem(
                sentence = "The quick brown fox jumps over the lazy ___.",
                answer = "do",
                isSubmitted = false,
                isCorrect = false,
                isWrong = false,
                correctAnswer = "dog",
                itemNumber = 1,
                onAnswerChange = {}
            )
        }
    }
}
