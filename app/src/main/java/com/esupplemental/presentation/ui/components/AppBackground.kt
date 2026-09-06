package com.esupplemental.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import kotlin.math.sin

/**
 * The standard background for all screens in the application.
 * Supports dynamic color transitions and floating decorative "blobs" for a playful look.
 */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    topColor: Color = MaterialTheme.colorScheme.background,
    bottomColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    showBlobs: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    // 1. Smooth Color Transitions
    val animatedTop by animateColorAsState(
        targetValue = topColor,
        animationSpec = tween(durationMillis = 800),
        label = "TopBg"
    )
    val animatedBottom by animateColorAsState(
        targetValue = bottomColor,
        animationSpec = tween(durationMillis = 800),
        label = "BottomBg"
    )

    // Create the continuous animation only on screens that actually draw the blobs.
    val waveState = if (showBlobs) {
        val infiniteTransition = rememberInfiniteTransition(label = "blob_motion")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 2f * Math.PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(5000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "wave"
        )
    } else {
        null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(animatedTop, animatedBottom)
                    )
                )
            }
    ) {
        if (showBlobs && waveState != null) {
            val blobColor = MaterialTheme.colorScheme.primary
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val wave = waveState.value

                val offsetX = (sin(wave) * 40).dp.toPx()
                val offsetY = (sin(wave * 0.8f) * 30).dp.toPx()

                // Decorative "Energy" Blobs
                // Top Left - Theme Accent Flare
                drawBlob(
                    center = Offset(-w * 0.05f + offsetX, h * 0.1f + offsetY),
                    radius = w * 0.5f,
                    color = blobColor,
                    alpha = 0.05f
                )

                // Bottom Right - Secondary Accent
                drawBlob(
                    center = Offset(w * 1.05f - offsetX, h * 0.9f - offsetY),
                    radius = w * 0.6f,
                    color = animatedBottom,
                    alpha = 0.15f
                )

                // Center Float - Soft Light Glow
                drawBlob(
                    center = Offset(w * 0.8f + (offsetX * -0.5f), h * 0.3f + (offsetY * 1.2f)),
                    radius = w * 0.25f,
                    color = Color.White,
                    alpha = 0.03f
                )
            }
        }

        content()
    }
}

private fun DrawScope.drawBlob(center: Offset, radius: Float, color: Color, alpha: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

@Preview(showBackground = true, name = "Background Light")
@Composable
fun AppBackgroundLightPreview() {
    ESupplementalTheme(darkTheme = false) {
        AppBackground {
            Text("Preview Content", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Preview(showBackground = true, name = "Background Dark")
@Composable
fun AppBackgroundDarkPreview() {
    ESupplementalTheme(darkTheme = true) {
        AppBackground {
            Text("Preview Content", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Preview(showBackground = true, name = "Listening Mode - Playful")
@Composable
fun AppBackgroundPlayfulPreview() {
    // Grade 6 "Music/Listening" vibe using theme tokens or Arcade colors
    ESupplementalTheme {
        AppBackground(
            topColor = ArcadeColors.Navy,
            bottomColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Listening Time!",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}
