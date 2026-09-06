package com.esupplemental.presentation.ui.screens.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.state.PlayerUiState
import com.esupplemental.presentation.ui.theme.BrandNavy
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PlaybackControls(
    state: PlayerUiState,
    accentColor: Color,
    onPlayPause: () -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onSeek: (Float) -> Unit
) {
    val totalPos = if (state.totalDurationSeconds > 0) 
        state.totalDurationSeconds.toFloat() 
    else 
        (state.mediaItem?.durationSeconds?.toFloat() ?: 1f)
    
    // Local state to track the slider value during user interaction
    // This prevents the slider from jumping back to the actual playback position 
    // while the user is still dragging.
    var draggingPosition by remember { mutableStateOf<Float?>(null) }
    
    val currentPos = draggingPosition ?: state.currentPositionSeconds.toFloat()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── ERROR MESSAGE ──
        AnimatedVisibility(
            visible = state.error != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.ErrorOutline,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = state.error ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── CHUNKY PROGRESS SLIDER ──
        Column {
            Slider(
                value = currentPos,
                onValueChange = { draggingPosition = it },
                onValueChangeFinished = {
                    draggingPosition?.let {
                        // Pass the fraction (0.0 to 1.0) to the ViewModel
                        onSeek(it / totalPos.coerceAtLeast(1f))
                        draggingPosition = null
                    }
                },
                valueRange = 0f..totalPos,
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = accentColor.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPos.toInt()),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTime(totalPos.toInt()),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── ARCADE BUTTON ROW ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Skip Backward Button
            SeekButton(
                icon = Icons.Rounded.Replay10,
                contentDescription = "Back 10s",
                accentColor = accentColor,
                onClick = onSkipBackward,
                isForward = false
            )

            Spacer(Modifier.width(24.dp))

            // Play/Pause "Power Button"
            Surface(
                onClick = onPlayPause,
                shape = CircleShape,
                color = accentColor,
                shadowElevation = 8.dp,
                modifier = Modifier.size(82.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (state.isBuffering) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 4.dp,
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(24.dp))

            // Skip Forward Button
            SeekButton(
                icon = Icons.Rounded.Forward10,
                contentDescription = "Forward 10s",
                accentColor = accentColor,
                onClick = onSkipForward,
                isForward = true
            )
        }
    }
}

@Composable
private fun SeekButton(
    icon: ImageVector,
    contentDescription: String,
    accentColor: Color,
    onClick: () -> Unit,
    isForward: Boolean
) {
    var isAnimating by remember { mutableStateOf(false) }
    
    val rotation by animateFloatAsState(
        targetValue = if (isAnimating) (if (isForward) 45f else -45f) else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "seek_rotation"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.2f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "seek_scale"
    )

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(200.milliseconds)
            isAnimating = false
        }
    }

    IconButton(
        onClick = {
            isAnimating = true
            onClick()
        },
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer {
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
            }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = accentColor,
            modifier = Modifier.size(32.dp)
        )
    }
}

// Helper to format seconds into 00:00
private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

@Preview(showBackground = true, name = "Arcade Controls - Playing")
@Composable
fun PlaybackControlsPreview() {
    ESupplementalTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            PlaybackControls(
                state = PlayerUiState(
                    isPlaying = true,
                    currentPositionSeconds = 45
                ),
                accentColor = BrandNavy,
                onPlayPause = {},
                onSkipForward = {},
                onSkipBackward = {},
                onSeek = {}
            )
        }
    }
}