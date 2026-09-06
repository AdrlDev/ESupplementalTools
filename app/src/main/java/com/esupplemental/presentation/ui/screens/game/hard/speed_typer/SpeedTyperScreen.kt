package com.esupplemental.presentation.ui.screens.game.hard.speed_typer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.presentation.state.game_state.SpeedTyperPhase
import com.esupplemental.presentation.state.game_state.SpeedTyperUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.*
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.SpeedTyperViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SpeedTyperScreen(
    gameId: String,
    mediaId: String? = null,
    onBack: () -> Unit,
    viewModel: SpeedTyperViewModel = koinViewModel(parameters = { parametersOf(gameId, mediaId) })
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = MaterialTheme.spacing

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenPadding)
                .padding(bottom = spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            val statusText = when (state.phase) {
                SpeedTyperPhase.IDLE -> "Listen carefully, then type exactly what you heard!"
                SpeedTyperPhase.LOADING -> "Preparing audio segment..."
                SpeedTyperPhase.LISTENING -> "Audio playing... Listen closely!"
                SpeedTyperPhase.TYPING -> "Type the excerpt now!"
                SpeedTyperPhase.CORRECT -> "Perfect accuracy!"
                SpeedTyperPhase.WRONG -> "Almost! Keep practicing."
                SpeedTyperPhase.GAME_OVER -> "Well done! Check your score."
            }

            GameTopBar(
                title = "Speed Typer",
                subtitle = statusText,
                icon = Icons.Rounded.Bolt,
                accent = ArcadeColors.Warning,
                onBack = onBack,
                currentStep = state.round.takeIf { it > 0 },
                totalSteps = state.totalRounds.takeIf { it > 0 },
                score = state.score,
                xp = state.xpEarned
            )

            Spacer(Modifier.height(spacing.large))

            AnimatedContent(
                targetState = state.phase,
                transitionSpec = {
                    fadeIn(tween(400)) togetherWith fadeOut(tween(300))
                },
                label = "speed_typer_content"
            ) { phase ->
                when (phase) {
                    SpeedTyperPhase.IDLE -> {
                        GameStartContent(onStart = viewModel::startGame)
                    }
                    SpeedTyperPhase.LOADING -> {
                        GameLoadingState(message = "Loading story excerpt...")
                    }
                    SpeedTyperPhase.LISTENING -> {
                        ListeningContent()
                    }
                    SpeedTyperPhase.TYPING, SpeedTyperPhase.CORRECT, SpeedTyperPhase.WRONG -> {
                        TypingContent(
                            state = state,
                            onValueChange = viewModel::onUserInputChange,
                            onSubmit = viewModel::submitAnswer
                        )
                    }
                    SpeedTyperPhase.GAME_OVER -> {
                        GameResultScreen(
                            score = state.score,
                            total = state.totalRounds * 25, // Max potential score per round
                            xp = state.xpEarned,
                            onPlayAgain = viewModel::startGame,
                            onBack = onBack,
                            accent = ArcadeColors.Warning
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameStartContent(onStart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(24.dp)
    ) {
        Text(
            "Ready for a challenge?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "You will hear a phrase from the story. Type it back as fast and accurately as possible!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        GamePrimaryButton(
            text = "START CHALLENGE",
            onClick = onStart,
            icon = Icons.Rounded.PlayArrow,
            accent = ArcadeColors.Warning
        )
    }
}

@Composable
private fun ListeningContent() {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition(label = "listening_equalizer")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.medium)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ArcadeColors.Warning.copy(alpha = 0.7f * pulseAlpha),
                        colorScheme.primary.copy(alpha = 0.2f)
                    )
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 20.dp)
            ) {
                // Pulsing Listening Badge Icon
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(68.dp)
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
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Animated Equalizer Waveform
                EqualizerWaveform(infiniteTransition = infiniteTransition)

                Spacer(Modifier.height(24.dp))

                // Title
                Text(
                    text = "Listen Closely...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ArcadeColors.Warning,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                // Reminder Sub-text
                Text(
                    text = "Listen carefully to every word! The audio only plays once, and your typing countdown starts right after.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun EqualizerWaveform(infiniteTransition: InfiniteTransition) {
    val barCount = 9
    val barMaxHeights = listOf(24, 42, 30, 54, 36, 48, 28, 44, 22)

    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(58.dp)
    ) {
        repeat(barCount) { index ->
            val maxH = barMaxHeights.getOrElse(index) { 32 }
            val barScale by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 350 + (index % 4) * 100,
                        delayMillis = index * 50,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "eq_bar_$index"
            )

            val barBrush = if (index % 2 == 0) {
                Brush.verticalGradient(
                    colors = listOf(
                        ArcadeColors.Warning,
                        ArcadeColors.Warning.copy(alpha = 0.55f)
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                    )
                )
            }

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height((maxH * barScale + 8).dp)
                    .clip(CircleShape)
                    .background(barBrush)
            )
        }
    }
}

@Composable
private fun TypingContent(
    state: SpeedTyperUiState,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val story = state.storyContext

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Story Context Header
        if (story != null) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = spacing.medium)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                        Icon(Icons.Rounded.Bolt, null, modifier = Modifier.align(Alignment.Center), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "STORY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(text = story.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        if (state.phase == SpeedTyperPhase.TYPING) {
            GameTimer(remainingMs = state.timerMs, totalMs = state.maxTimerMs)
            Spacer(Modifier.height(spacing.medium))
        }

        OutlinedTextField(
            value = state.userInput,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
            label = { Text("What did you hear?") },
            placeholder = { Text("Type here...") },
            shape = MaterialTheme.shapes.large,
            enabled = state.phase == SpeedTyperPhase.TYPING,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ArcadeColors.Warning,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(Modifier.height(spacing.large))

        when (state.phase) {
            SpeedTyperPhase.CORRECT -> {
                GameFeedbackBanner(
                    title = "Perfect!",
                    message = "You nailed it.",
                    type = GameFeedbackType.SUCCESS
                )
            }
            SpeedTyperPhase.WRONG -> {
                GameFeedbackBanner(
                    title = "Not quite!",
                    message = "The phrase was:\n\"${state.currentPhrase}\"",
                    type = GameFeedbackType.ENCOURAGEMENT
                )
            }
            else -> {
                GamePrimaryButton(
                    text = "SUBMIT",
                    onClick = onSubmit,
                    icon = Icons.Rounded.CheckCircle,
                    accent = ArcadeColors.Warning,
                    enabled = state.userInput.isNotBlank()
                )
            }
        }
    }
}

@Preview(
    name = "Speed Typer - Typing",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun SpeedTyperTypingPreview() {
    ESupplementalTheme {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameTopBar(
                    title = "Speed Typer",
                    subtitle = "Type the excerpt now!",
                    icon = Icons.Rounded.Bolt,
                    accent = ArcadeColors.Warning,
                    onBack = {},
                    currentStep = 2,
                    totalSteps = 5,
                    score = 35,
                    xp = 10
                )

                Spacer(Modifier.height(24.dp))

                TypingContent(
                    state = SpeedTyperUiState(
                        phase = SpeedTyperPhase.TYPING,
                        round = 2,
                        totalRounds = 5,
                        score = 35,
                        xpEarned = 10,
                        userInput = "Once upon a time",
                        currentPhrase = "Once upon a time in a quiet town",
                        timerMs = 12_000L,
                        maxTimerMs = 20_000L
                    ),
                    onValueChange = {},
                    onSubmit = {}
                )
            }
        }
    }
}

@Preview(
    name = "Speed Typer - Listening",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun SpeedTyperListeningPreview() {
    ESupplementalTheme {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameTopBar(
                    title = "Speed Typer",
                    subtitle = "Audio playing... Listen closely!",
                    icon = Icons.Rounded.Bolt,
                    accent = ArcadeColors.Warning,
                    onBack = {},
                    currentStep = 1,
                    totalSteps = 5,
                    score = 0,
                    xp = 0
                )

                Spacer(Modifier.height(32.dp))

                ListeningContent()
            }
        }
    }
}

@Preview(
    name = "Speed Typer - Wrong Answer",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun SpeedTyperWrongPreview() {
    ESupplementalTheme {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameTopBar(
                    title = "Speed Typer",
                    subtitle = "Almost! Keep practicing.",
                    icon = Icons.Rounded.Bolt,
                    accent = ArcadeColors.Warning,
                    onBack = {},
                    currentStep = 3,
                    totalSteps = 5,
                    score = 48,
                    xp = 15
                )

                Spacer(Modifier.height(24.dp))

                TypingContent(
                    state = SpeedTyperUiState(
                        phase = SpeedTyperPhase.WRONG,
                        round = 3,
                        totalRounds = 5,
                        score = 48,
                        xpEarned = 15,
                        userInput = "The boy went to the river",
                        currentPhrase = "The boy quietly walked toward the river",
                        timerMs = 0L,
                        maxTimerMs = 20_000L
                    ),
                    onValueChange = {},
                    onSubmit = {}
                )
            }
        }
    }
}
