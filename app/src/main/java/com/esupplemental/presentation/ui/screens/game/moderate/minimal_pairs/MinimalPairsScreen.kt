package com.esupplemental.presentation.ui.screens.game.moderate.minimal_pairs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.data.model.game.MinimalPairQuestion
import com.esupplemental.presentation.state.game_state.MinimalPairsUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.GameFeedbackBanner
import com.esupplemental.presentation.ui.screens.game.GameFeedbackType
import com.esupplemental.presentation.ui.screens.game.GamePrimaryButton
import com.esupplemental.presentation.ui.screens.game.GameResultScreen
import com.esupplemental.presentation.ui.screens.game.GameTopBar
import com.esupplemental.presentation.ui.screens.game.moderate.MinimalPairsPhase
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.MinimalPairsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MinimalPairsScreen(
    gameId: String,
    mediaId: String? = null,
    onBack: () -> Unit,
    vm: MinimalPairsViewModel = koinViewModel(parameters = { parametersOf(gameId, mediaId) })
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    AppBackground {
        MinimalPairsContent(
            state = state,
            onStart = vm::startGame,
            onBack = onBack,
            onPlayAudio = vm::playAudio,
            onSelect = vm::onWordSelected
        )
    }
}

@Composable
fun MinimalPairsContent(
    state: MinimalPairsUiState,
    onStart: () -> Unit,
    onBack: () -> Unit,
    onPlayAudio: () -> Unit,
    onSelect: (String) -> Unit
) {
    val spacing = MaterialTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.screenPadding)
            .padding(bottom = spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(52.dp))

        GameTopBar(
            title = "Minimal Pairs Battle",
            subtitle = when (state.phase) {
                MinimalPairsPhase.IDLE -> "Listen closely and choose the word you hear."
                MinimalPairsPhase.LISTENING -> "Sound incoming—focus on every syllable."
                MinimalPairsPhase.SELECTING -> "Choose the exact word that was spoken."
                MinimalPairsPhase.CORRECT -> "Sharp ears—great answer!"
                MinimalPairsPhase.WRONG -> "Almost! Compare the sounds and try again."
                MinimalPairsPhase.GAME_OVER -> "Listening battle complete!"
            },
            icon = Icons.Rounded.Hearing,
            accent = ArcadeColors.Navy,
            onBack = onBack,
            currentStep = state.round.takeIf { state.phase != MinimalPairsPhase.IDLE && state.phase != MinimalPairsPhase.GAME_OVER },
            totalSteps = state.totalRounds.takeIf { state.phase != MinimalPairsPhase.IDLE && state.phase != MinimalPairsPhase.GAME_OVER },
            score = state.score.takeIf { state.phase != MinimalPairsPhase.IDLE },
            xp = state.xpEarned
        )

        Spacer(Modifier.height(spacing.large))

        AnimatedContent(
            targetState = state.phase,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn(tween(350))).togetherWith(
                    slideOutHorizontally { -it } + fadeOut(tween(200))
                )
            },
            label = "phase_transition"
        ) { phase ->
            when (phase) {
                MinimalPairsPhase.IDLE -> MinimalPairsStartScreen(
                    state = state,
                    onStart = onStart
                )
                MinimalPairsPhase.LISTENING,
                MinimalPairsPhase.SELECTING -> GameplayPhase(
                    state = state,
                    onPlayAudio = onPlayAudio,
                    onSelect = onSelect
                )
                MinimalPairsPhase.CORRECT,
                MinimalPairsPhase.WRONG -> FeedbackPhase(state = state)
                MinimalPairsPhase.GAME_OVER -> GameResultScreen(
                    score = state.score,
                    total = state.totalRounds,
                    xp = state.xpEarned,
                    onPlayAgain = onStart,
                    onBack = onBack,
                    accent = ArcadeColors.Navy
                )
            }
        }
    }
}

@Composable
private fun MinimalPairsStartScreen(
    state: MinimalPairsUiState,
    onStart: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition(label = "mp_hero_bounce")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_scale"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .size(116.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        listOf(
                            ArcadeColors.Navy.copy(alpha = 0.28f),
                            ArcadeColors.Navy.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(2.5.dp, ArcadeColors.Navy.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.RecordVoiceOver,
                contentDescription = null,
                modifier = Modifier.size(62.dp),
                tint = ArcadeColors.Navy
            )
        }

        Surface(
            color = colorScheme.surface.copy(alpha = 0.75f),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, colorScheme.outlineVariant.copy(alpha = 0.7f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(spacing.large),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                Text(
                    text = "How to Play Sound Battle",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ArcadeColors.Navy
                )
                Text(
                    text = "1. 👂 Listen to the spoken story word.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "2. 🔍 Compare the two similar-sounding choices.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "3. 🎯 Tap the exact word you heard to score!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        state.errorMessage?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        GamePrimaryButton(
            text = when {
                state.isContentLoading -> "LOADING STORY SOUNDS…"
                state.errorMessage != null -> "TRY AGAIN"
                else -> "START SOUND BATTLE"
            },
            onClick = onStart,
            enabled = !state.isContentLoading,
            icon = Icons.Rounded.PlayArrow.takeUnless { state.isContentLoading },
            accent = ArcadeColors.Navy
        )
    }
}

@Composable
private fun GameplayPhase(
    state: MinimalPairsUiState,
    onPlayAudio: () -> Unit,
    onSelect: (String) -> Unit
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme

    val isListening = state.phase == MinimalPairsPhase.LISTENING
    val isAudioBusy = state.isAudioLoading || state.isAudioPlaying

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        modifier = Modifier.fillMaxWidth()
    ) {
        StoryContextCard()

        if (isListening) {
            MinimalPairsEqualizerBanner()
        } else {
            // Tactile Replay Button with glowing sound wave aura
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ArcadeColors.Navy.copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ArcadeColors.Navy.copy(alpha = 0.95f),
                                    ArcadeColors.Navy.copy(alpha = 0.70f)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.15f))
                            ),
                            shape = CircleShape
                        )
                        .clickable(enabled = !isAudioBusy) { onPlayAudio() },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isAudioLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "Replay sound",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Text(
                text = "Tap speaker to hear again",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = ArcadeColors.Navy
            )

            state.errorMessage?.let { error ->
                Text(
                    text = "Audio unavailable: $error",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(spacing.small))

            // Playful Sound Showdown Prompt
            Surface(
                color = ArcadeColors.Navy.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ArcadeColors.Navy.copy(alpha = 0.2f)),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        tint = ArcadeColors.Navy,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Choose the word with the matching sound:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ArcadeColors.Navy
                    )
                }
            }

            // Playful 3D Arcade Sound Battle Options with VS Divider
            state.currentQuestion?.let { question ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayfulWordCard(
                        optionLabel = "CHOICE A",
                        word = question.wordA,
                        isSelected = state.selectedWord == question.wordA,
                        accentColor = ArcadeColors.Navy,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelect(question.wordA) }
                    )

                    // Playful VS Badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ArcadeColors.Reward.copy(alpha = 0.18f))
                            .border(1.5.dp, ArcadeColors.Reward, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = ArcadeColors.Reward
                        )
                    }

                    PlayfulWordCard(
                        optionLabel = "CHOICE B",
                        word = question.wordB,
                        isSelected = state.selectedWord == question.wordB,
                        accentColor = ArcadeColors.Teal,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelect(question.wordB) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayfulWordCard(
    optionLabel: String,
    word: String,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(),
        label = "word_card_scale"
    )

    Surface(
        color = if (isSelected) accentColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = if (isSelected) 2.5.dp else 1.5.dp,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .scale(scaleAnim)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp)
        ) {
            // Option Tag Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = if (isSelected) 0.25f else 0.12f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = optionLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            }

            Spacer(Modifier.height(14.dp))

            // Big Bold Phonics Word
            Text(
                text = word,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                fontWeight = FontWeight.Black,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Selected",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Tap to choose",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun StoryContextCard() {
    Surface(
        color = ArcadeColors.Navy.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ArcadeColors.Navy.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ArcadeColors.Navy.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoStories,
                    contentDescription = null,
                    tint = ArcadeColors.Navy,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "STORY PHONICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcadeColors.Navy,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Minimal Sound Pairs from Story Reading",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun MinimalPairsEqualizerBanner() {
    val infiniteTransition = rememberInfiniteTransition(label = "mp_equalizer")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    ArcadeColors.Navy.copy(alpha = 0.7f * pulseAlpha),
                    ArcadeColors.Navy.copy(alpha = 0.2f)
                )
            )
        ),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ArcadeColors.Navy.copy(alpha = 0.35f),
                                ArcadeColors.Navy.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = ArcadeColors.Navy.copy(alpha = 0.85f * pulseAlpha),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Hearing,
                    contentDescription = "Listening",
                    tint = ArcadeColors.Navy,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            MinimalPairsEqualizerWaveform(infiniteTransition = infiniteTransition)

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Listen Closely to the Pronunciation...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ArcadeColors.Navy,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Two similar-sounding words are waiting. Focus on the subtle vowel/consonant sound!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun MinimalPairsEqualizerWaveform(infiniteTransition: InfiniteTransition) {
    val barCount = 7
    val barMaxHeights = listOf(20, 36, 26, 48, 30, 42, 22)

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(50.dp)
    ) {
        repeat(barCount) { index ->
            val maxH = barMaxHeights.getOrElse(index) { 28 }
            val barScale by infiniteTransition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 350 + (index % 3) * 110,
                        delayMillis = index * 55,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "mp_bar_$index"
            )

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height((maxH * barScale + 6).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ArcadeColors.Navy,
                                ArcadeColors.Navy.copy(alpha = 0.45f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun FeedbackPhase(state: MinimalPairsUiState) {
    val isCorrect = state.phase == MinimalPairsPhase.CORRECT
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        GameFeedbackBanner(
            title = if (isCorrect) "Spot on! 🎯" else "Good Try! 👂",
            message = if (isCorrect) {
                "Awesome listening! You correctly identified '${state.currentQuestion?.correctWord.orEmpty()}'."
            } else {
                "The spoken word was '${state.currentQuestion?.correctWord.orEmpty()}'."
            },
            type = if (isCorrect) GameFeedbackType.SUCCESS else GameFeedbackType.ENCOURAGEMENT
        )

        Spacer(Modifier.height(16.dp))

        // Contrast Comparison Card
        state.currentQuestion?.let { question ->
            Surface(
                color = if (isCorrect) ArcadeColors.Emerald.copy(alpha = 0.12f) else ArcadeColors.Rose.copy(alpha = 0.12f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.5.dp,
                    if (isCorrect) ArcadeColors.Emerald else ArcadeColors.Rose
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Correct Word: ${question.correctWord.uppercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = if (isCorrect) ArcadeColors.Emerald else ArcadeColors.Rose
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Pair comparison: “${question.wordA}” vs “${question.wordB}”",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(
    name = "Minimal Pairs - Gameplay",
    showBackground = true,
    widthDp = 412,
    heightDp = 892
)
@Composable
private fun MinimalPairsGameplayPreview() = MinimalPairsPreview(
    state = MinimalPairsUiState(
        isContentLoading = false,
        phase = MinimalPairsPhase.SELECTING,
        currentQuestion = previewMinimalPair,
        selectedWord = "sheep",
        round = 2,
        totalRounds = 8,
        score = 1,
        xpEarned = 3
    )
)

@Preview(
    name = "Minimal Pairs - Correct Feedback",
    showBackground = true,
    widthDp = 412,
    heightDp = 892
)
@Composable
private fun MinimalPairsFeedbackPreview() = MinimalPairsPreview(
    state = MinimalPairsUiState(
        isContentLoading = false,
        phase = MinimalPairsPhase.CORRECT,
        currentQuestion = previewMinimalPair,
        selectedWord = "sheep",
        isCorrect = true,
        round = 3,
        totalRounds = 8,
        score = 3,
        xpEarned = 6
    )
)

@Composable
private fun MinimalPairsPreview(state: MinimalPairsUiState) {
    ESupplementalTheme(darkTheme = false, dynamicColor = false) {
        AppBackground(showBlobs = false) {
            MinimalPairsContent(
                state = state,
                onStart = {},
                onBack = {},
                onPlayAudio = {},
                onSelect = {}
            )
        }
    }
}

private val previewMinimalPair = MinimalPairQuestion(
    id = "preview_ship_sheep",
    wordA = "ship",
    wordB = "sheep",
    correctWord = "sheep"
)
