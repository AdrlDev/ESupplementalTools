package com.esupplemental.presentation.ui.screens.game.moderate.two_truths_lie

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
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
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
import com.esupplemental.presentation.state.game_state.TwoTruthsLieUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.GameFeedbackBanner
import com.esupplemental.presentation.ui.screens.game.GameFeedbackType
import com.esupplemental.presentation.ui.screens.game.GamePrimaryButton
import com.esupplemental.presentation.ui.screens.game.GameResultScreen
import com.esupplemental.presentation.ui.screens.game.GameTopBar
import com.esupplemental.presentation.ui.screens.game.moderate.TwoTruthsLiePhase
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.TwoTruthsLieViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TwoTruthsLieScreen(
    gameId: String,
    mediaId: String? = null,
    onBack: () -> Unit,
    vm: TwoTruthsLieViewModel = koinViewModel(parameters = { parametersOf(gameId, mediaId) })
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    AppBackground {
        TwoTruthsLieContent(
            state = state,
            onStart = vm::startGame,
            onBack = onBack,
            onPlayAudio = vm::playAudio,
            onSelect = vm::onStatementSelected,
            onSubmit = vm::submitAnswer
        )
    }
}

@Composable
fun TwoTruthsLieContent(
    state: TwoTruthsLieUiState,
    onStart: () -> Unit,
    onBack: () -> Unit,
    onPlayAudio: () -> Unit,
    onSelect: (Int) -> Unit,
    onSubmit: () -> Unit
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
            title = "2 Truths 1 Lie",
            subtitle = when (state.phase) {
                TwoTruthsLiePhase.IDLE -> "Listen to story clues and spot the impostor!"
                TwoTruthsLiePhase.LISTENING -> "Clues incoming—focus and remember details."
                TwoTruthsLiePhase.SELECTING -> "Which statement was NOT spoken?"
                TwoTruthsLiePhase.CORRECT -> "Master Detective—you found the lie!"
                TwoTruthsLiePhase.WRONG -> "Good investigation! Review the clues."
                TwoTruthsLiePhase.GAME_OVER -> "Detective mission complete!"
            },
            icon = Icons.Rounded.Search,
            accent = ArcadeColors.Warning,
            onBack = onBack,
            currentStep = state.round.takeIf { state.phase != TwoTruthsLiePhase.IDLE && state.phase != TwoTruthsLiePhase.GAME_OVER },
            totalSteps = state.totalRounds.takeIf { state.phase != TwoTruthsLiePhase.IDLE && state.phase != TwoTruthsLiePhase.GAME_OVER },
            score = state.score.takeIf { state.phase != TwoTruthsLiePhase.IDLE },
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
                TwoTruthsLiePhase.IDLE -> TwoTruthsLieStartScreen(state, onStart)
                TwoTruthsLiePhase.LISTENING -> ListeningPhase(state)
                TwoTruthsLiePhase.SELECTING -> SelectionPhase(
                    state = state,
                    onPlayAudio = onPlayAudio,
                    onSelect = onSelect,
                    onSubmit = onSubmit
                )
                TwoTruthsLiePhase.CORRECT,
                TwoTruthsLiePhase.WRONG -> FeedbackPhase(state)
                TwoTruthsLiePhase.GAME_OVER -> GameResultScreen(
                    score = state.score,
                    total = state.totalRounds,
                    xp = state.xpEarned,
                    onPlayAgain = onStart,
                    onBack = onBack,
                    accent = ArcadeColors.Warning
                )
            }
        }
    }
}

@Composable
private fun TwoTruthsLieStartScreen(
    state: TwoTruthsLieUiState,
    onStart: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition(label = "detective_hero_bounce")

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
                            ArcadeColors.Warning.copy(alpha = 0.28f),
                            ArcadeColors.Warning.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(2.5.dp, ArcadeColors.Warning.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                modifier = Modifier.size(62.dp),
                tint = ArcadeColors.Warning
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
                    text = "🕵️ How to Play Detective",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ArcadeColors.Warning
                )
                Text(
                    text = "1. 🎧 Listen closely to two audio clues taken from the story.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "2. 📜 Read all 3 statement cards carefully.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "3. 🎯 Spot the ONE statement that was NOT spoken!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        GamePrimaryButton(
            text = "START DETECTIVE MISSION",
            onClick = onStart,
            enabled = !state.isContentLoading,
            icon = Icons.Rounded.PlayArrow,
            accent = ArcadeColors.Warning
        )
    }
}

@Composable
private fun ListeningPhase(
    state: TwoTruthsLieUiState
) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.currentQuestion?.let { question ->
            StoryContextCard(title = question.sourceTitle)
            Spacer(Modifier.height(spacing.medium))
        }

        TwoTruthsEqualizerBanner(
        )

        Spacer(Modifier.height(spacing.medium))

        state.audioError?.let { error ->
            Text(
                text = "Audio error: $error\nYou can continue with text clues.",
                style = typography.bodySmall,
                color = colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(spacing.small))
        }
    }
}

@Composable
private fun SelectionPhase(
    state: TwoTruthsLieUiState,
    onPlayAudio: () -> Unit,
    onSelect: (Int) -> Unit,
    onSubmit: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val question = state.currentQuestion ?: return
    val isAudioBusy = state.isAudioLoading || state.isAudioPlaying

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        StoryContextCard(title = question.sourceTitle)

        // Tactile Replay Speaker with glowing wave aura
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(96.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ArcadeColors.Warning.copy(alpha = 0.22f),
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
                                ArcadeColors.Warning.copy(alpha = 0.95f),
                                ArcadeColors.Warning.copy(alpha = 0.70f)
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
                    .clickable(
                        enabled = !isAudioBusy,
                        onClick = onPlayAudio
                    ),
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
                        contentDescription = "Replay audio clues",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        Text(
            text = "Tap speaker to hear story clues again",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = ArcadeColors.Warning
        )

        Spacer(Modifier.height(spacing.small))

        // Playful Clue Prompt Chip
        Surface(
            color = ArcadeColors.Warning.copy(alpha = 0.08f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, ArcadeColors.Warning.copy(alpha = 0.25f)),
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = ArcadeColors.Warning,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Spot the Impostor: Which clue was NOT spoken?",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ArcadeColors.Warning
                )
            }
        }

        // Playful 3D Detective Clue Cards
        question.statements.forEachIndexed { index, statement ->
            val isSelected = state.selectedIndex == index
            DetectiveClueCard(
                clueNumber = ('A' + index).toString(),
                statementText = statement.text,
                isSelected = isSelected,
                onClick = { onSelect(index) }
            )
        }

        Spacer(Modifier.height(spacing.small))

        GamePrimaryButton(
            text = "LOCK IN ANSWER",
            onClick = onSubmit,
            icon = Icons.Rounded.CheckCircle,
            enabled = state.selectedIndex != null,
            accent = ArcadeColors.Warning
        )
    }
}

@Composable
private fun DetectiveClueCard(
    clueNumber: String,
    statementText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1.0f,
        animationSpec = spring(),
        label = "clue_card_scale"
    )

    Surface(
        color = if (isSelected) ArcadeColors.Warning.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) ArcadeColors.Warning else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleAnim)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clue Badge Box
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) ArcadeColors.Warning else ArcadeColors.Warning.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = clueNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) Color.White else ArcadeColors.Warning
                )
            }

            Spacer(Modifier.width(14.dp))

            Text(
                text = statementText,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Selected",
                    tint = ArcadeColors.Warning,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun StoryContextCard(title: String) {
    Surface(
        color = ArcadeColors.Warning.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ArcadeColors.Warning.copy(alpha = 0.25f)),
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
                    .background(ArcadeColors.Warning.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoStories,
                    contentDescription = null,
                    tint = ArcadeColors.Warning,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DETECTIVE CASE FILE",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcadeColors.Warning,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title.ifBlank { "Story Clues" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun TwoTruthsEqualizerBanner() {
    val infiniteTransition = rememberInfiniteTransition(label = "two_truths_eq")
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
                    ArcadeColors.Warning.copy(alpha = 0.7f * pulseAlpha),
                    ArcadeColors.Warning.copy(alpha = 0.2f)
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
                                ArcadeColors.Warning.copy(alpha = 0.35f),
                                ArcadeColors.Warning.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = ArcadeColors.Warning.copy(alpha = 0.85f * pulseAlpha),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Hearing,
                    contentDescription = "Listening",
                    tint = ArcadeColors.Warning,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            TwoTruthsEqualizerWaveform(infiniteTransition = infiniteTransition)

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Listen Closely to the Clues...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ArcadeColors.Warning,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Pay close attention! The statements are hidden while the audio plays. Find the one that was NOT spoken.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun TwoTruthsEqualizerWaveform(infiniteTransition: InfiniteTransition) {
    val barCount = 7
    val barMaxHeights = listOf(22, 38, 28, 46, 32, 40, 24)

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
                        durationMillis = 360 + (index % 3) * 100,
                        delayMillis = index * 50,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "tt_bar_$index"
            )

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height((maxH * barScale + 6).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ArcadeColors.Warning,
                                ArcadeColors.Warning.copy(alpha = 0.45f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun FeedbackPhase(state: TwoTruthsLieUiState) {
    val isCorrect = state.phase == TwoTruthsLiePhase.CORRECT
    val unspokenStatement = state.currentQuestion
        ?.statements
        ?.firstOrNull { !it.isTrue }
        ?.text

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        GameFeedbackBanner(
            title = if (isCorrect) "Case Solved! 🕵️‍♂️" else "Good Detective Work! 🔍",
            message = if (isCorrect) {
                "Brilliant! You identified the unspoken clue."
            } else {
                "The statement not spoken was: “${unspokenStatement.orEmpty()}”"
            },
            type = if (isCorrect) GameFeedbackType.SUCCESS else GameFeedbackType.ENCOURAGEMENT
        )

        Spacer(Modifier.height(16.dp))

        // Solved Clues Breakdown
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
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🕵️ Case Clue Breakdown:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = if (isCorrect) ArcadeColors.Emerald else ArcadeColors.Rose
                    )

                    question.statements.forEach { stmt ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (stmt.isTrue) "✅ Spoken Truth:" else "🚫 The Lie:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (stmt.isTrue) ArcadeColors.Emerald else ArcadeColors.Rose
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stmt.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Two Truths - Selection Phase")
@Composable
fun TwoTruthsSelectionPreview() {
    ESupplementalTheme {
        AppBackground {
            TwoTruthsLieContent(
                state = TwoTruthsLieUiState(
                    phase = TwoTruthsLiePhase.SELECTING,
                    round = 2,
                    totalRounds = 5,
                    score = 20,
                    xpEarned = 10,
                    selectedIndex = 1
                ),
                onStart = {},
                onBack = {},
                onPlayAudio = {},
                onSelect = {},
                onSubmit = {}
            )
        }
    }
}
