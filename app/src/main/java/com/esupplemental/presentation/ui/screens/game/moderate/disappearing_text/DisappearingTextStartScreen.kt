package com.esupplemental.presentation.ui.screens.game.moderate.disappearing_text

import android.annotation.SuppressLint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.rememberGameSemanticColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.PlayfulShapes
import com.esupplemental.presentation.ui.theme.spacing

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun DisappearingTextStartScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onStart: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val semanticColors = rememberGameSemanticColors()

    // Hero icon floating bounce animation
    val infiniteTransition = rememberInfiniteTransition(label = "hero_bounce_transition")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_bounce"
    )

    // Glassmorphism base colors
    val baseContentColor = colorScheme.onSurface
    val glassBgTop = baseContentColor.copy(alpha = 0.06f)
    val glassBgBottom = baseContentColor.copy(alpha = 0.02f)
    val glassBorderTop = baseContentColor.copy(alpha = 0.18f)
    val glassBorderBottom = baseContentColor.copy(alpha = 0.04f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.medium)
        ) {
            // Hero Icon Circle with Radial Glow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                colorScheme.primary.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .background(
                            color = colorScheme.primary.copy(alpha = 0.15f),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.5.dp,
                            color = colorScheme.primary.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .offset(y = floatAnim.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoFixHigh,
                        contentDescription = "Disappearing Text",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            // Glassmorphic "How to Play" Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(glassBgTop, glassBgBottom)
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(glassBorderTop, glassBorderBottom)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(spacing.large)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                    Text(
                        text = "How to Play",
                        style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.onSurface
                    )

                    Spacer(Modifier.height(4.dp))

                    InstructionRow(
                        number = "1",
                        icon = Icons.Rounded.Visibility,
                        text = "Listen and follow each highlighted word"
                    )
                    InstructionRow(
                        number = "2",
                        icon = Icons.Rounded.Timer,
                        text = "Watch as words vanish one by one"
                    )
                    InstructionRow(
                        number = "3",
                        icon = Icons.Rounded.HistoryEdu,
                        text = "Type the full sentence from memory!"
                    )
                }
            }

            // Glassmorphic Tip Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.primary.copy(alpha = 0.12f),
                                colorScheme.primary.copy(alpha = 0.04f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.primary.copy(alpha = 0.4f),
                                colorScheme.primary.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(spacing.medium),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Star",
                        tint = semanticColors.star,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Focus hard and recall perfectly!",
                        style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            errorMessage?.let { error ->
                Text(
                    text = error,
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = semanticColors.warning,
                    textAlign = TextAlign.Center
                )
            }

            // Action Button
            Button(
                onClick = onStart,
                enabled = !isLoading,
                shape = PlayfulShapes.ActionButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp,
                            color = colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = when {
                            isLoading -> "Loading Stories..."
                            errorMessage != null -> "Try Again"
                            else -> "Start Game!"
                        },
                        style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun InstructionRow(
    number: String,
    icon: ImageVector,
    text: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .background(colorScheme.primary.copy(alpha = 0.2f), CircleShape)
        ) {
            Text(
                text = number,
                style = typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.primary
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = text,
            style = typography.bodyLarge,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DisappearingTextStartScreenPreview() {
    ESupplementalTheme {
        AppBackground {
            DisappearingTextStartScreen(onStart = {})
        }
    }
}
