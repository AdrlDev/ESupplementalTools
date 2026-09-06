package com.esupplemental.presentation.ui.screens.game.easy.story_order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.esupplemental.data.model.game.StoryQuestion
import com.esupplemental.domain.model.media.StoryChunk
import com.esupplemental.presentation.state.game_state.StoryOrderUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.GamePrimaryButton
import com.esupplemental.presentation.ui.screens.game.GameSecondaryButton
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing

@Composable
fun ListeningPhase(
    state: StoryOrderUiState,
    onPlayStory: () -> Unit,
    onStartOrdering: () -> Unit
) {
    val story = state.currentStory ?: return
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        // --- Story Header Card ---
        Surface(
            color = colorScheme.surfaceVariant.copy(alpha = 0.45f),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = story.thumbnailUrl ?: story.thumbnailRes,
                    contentDescription = "Cover image for ${story.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(18.dp))
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ArcadeColors.Teal.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoStories,
                            contentDescription = null,
                            tint = ArcadeColors.Teal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Black,
                        color = colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // --- Kid-Friendly Listening Helper Prompt ---
        Surface(
            color = ArcadeColors.Teal.copy(alpha = 0.10f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ArcadeColors.Teal.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Hearing,
                    contentDescription = null,
                    tint = ArcadeColors.Teal,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Listen carefully to the story sequence! Watch the highlighted words as the story plays.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = colorScheme.onSurface
                )
            }
        }

        // --- Audio Visualizer Equalizer Waveform ---
        AnimatedVisibility(visible = state.isAudioPlaying) {
            AudioWaveRow(infiniteTransition)
        }

        // --- Live Captions Banner with Word-by-Word Highlighting & Auto-Page Turning ---
        AnimatedVisibility(
            visible = state.isAudioPlaying && state.currentCaption != null
        ) {
            val chunk = state.currentChunk
            val startWordIndex = chunk?.startWordIndex ?: 0
            Surface(
                color = ArcadeColors.Teal.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, ArcadeColors.Teal.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "PAGE ${state.currentCaptionIndex + 1} OF ${state.captionParagraphs.size}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ArcadeColors.Teal
                    )
                    Spacer(Modifier.height(10.dp))
                    StoryWordByWordCaption(
                        captionText = state.currentCaption.orEmpty(),
                        captionStartWordIndex = startWordIndex,
                        currentWordIndex = state.currentWordIndex,
                        accentColor = ArcadeColors.Teal
                    )
                }
            }
        }

        // --- Play Audio Button ---
        GamePrimaryButton(
            text = when {
                state.isGeneratingAudio -> "PREPARING SHORT STORY…"
                state.isAudioBuffering -> "BUFFERING AUDIO…"
                state.isAudioPlaying -> "LISTENING TO STORY…"
                state.hasListened -> "PLAY STORY AGAIN"
                else -> "PLAY STORY NARRATION"
            },
            onClick = onPlayStory,
            enabled = !state.isAudioLoading,
            icon = when {
                state.isAudioPlaying -> Icons.Rounded.GraphicEq
                state.hasListened -> Icons.Rounded.Replay
                else -> Icons.Rounded.PlayArrow
            },
            accent = ArcadeColors.Teal
        )

        // --- Start Ordering Button ---
        GameSecondaryButton(
            text = "START ORDERING PUZZLE 🧩",
            onClick = onStartOrdering,
            icon = Icons.Rounded.Extension,
            accent = ArcadeColors.Reward
        )
    }
}

/**
 * Word-by-word dynamic audio highlighter with bouncy scale and glowing background on active word.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoryWordByWordCaption(
    captionText: String,
    captionStartWordIndex: Int,
    currentWordIndex: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val words = remember(captionText) {
        captionText.split(Regex("\\s+")).filter { it.isNotEmpty() }
    }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        words.forEachIndexed { relativeIndex, word ->
            val globalWordIndex = captionStartWordIndex + relativeIndex
            val isCurrentWord = globalWordIndex == currentWordIndex
            val isSpokenPastWord = globalWordIndex < currentWordIndex

            val scale by animateFloatAsState(
                targetValue = if (isCurrentWord) 1.15f else 1f,
                animationSpec = spring(),
                label = "word_scale_$relativeIndex"
            )

            val wordBgColor = when {
                isCurrentWord -> accentColor.copy(alpha = 0.25f)
                else -> Color.Transparent
            }

            val wordTextColor = when {
                isCurrentWord -> accentColor
                isSpokenPastWord -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
            }

            val wordWeight = if (isCurrentWord) FontWeight.Black else if (isSpokenPastWord) FontWeight.Bold else FontWeight.Medium

            Box(
                modifier = Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(6.dp))
                    .background(wordBgColor)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    fontWeight = wordWeight,
                    color = wordTextColor
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Listening: Initial State")
@Composable
fun ListeningPhaseInitialPreview() {
    val mockState = StoryOrderUiState(
        currentStory = StoryQuestion(
            id = "",
            title = "The Brave Little Toaster",
            storyText = "",
            events = emptyList()
        ),
        isAudioPlaying = false,
        hasListened = false
    )

    ESupplementalTheme {
        AppBackground {
            Box(modifier = Modifier.padding(20.dp)) {
                ListeningPhase(state = mockState, onPlayStory = {}, onStartOrdering = {})
            }
        }
    }
}
