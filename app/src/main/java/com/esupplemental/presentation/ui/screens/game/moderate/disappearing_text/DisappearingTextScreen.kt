package com.esupplemental.presentation.ui.screens.game.moderate.disappearing_text

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.data.model.game.DisappearingTextQuestion
import com.esupplemental.presentation.state.game_state.DisappearingTextUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.GameFeedbackBanner
import com.esupplemental.presentation.ui.screens.game.GameFeedbackType
import com.esupplemental.presentation.ui.screens.game.GamePrimaryButton
import com.esupplemental.presentation.ui.screens.game.GameResultScreen
import com.esupplemental.presentation.ui.screens.game.GameTimer
import com.esupplemental.presentation.ui.screens.game.GameTopBar
import com.esupplemental.presentation.ui.screens.game.moderate.DisappearingTextPhase
import com.esupplemental.presentation.ui.screens.game.rememberGameSemanticColors
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.PlayfulShapes
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.DisappearingTextViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DisappearingTextScreen(
    gameId: String,
    mediaId: String? = null,
    onBack: () -> Unit,
    vm: DisappearingTextViewModel = koinViewModel(parameters = { parametersOf(gameId, mediaId) })
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    AppBackground {
        DisappearingTextContent(
            state = state,
            onStart = vm::startGame,
            onBack = onBack,
            onInputChange = vm::onUserInputChange,
            onSubmit = vm::submitAnswer
        )
    }
}

@Composable
fun DisappearingTextContent(
    state: DisappearingTextUiState,
    onStart: () -> Unit,
    onBack: () -> Unit,
    onInputChange: (String) -> Unit,
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
            title = "Disappearing Text",
            subtitle = when (state.phase) {
                DisappearingTextPhase.IDLE -> "Listen, memorize, and rebuild the sentence."
                DisappearingTextPhase.MEMORIZING -> "Follow the words and lock them into memory."
                DisappearingTextPhase.DISAPPEARING -> "The words are vanishing—stay focused!"
                DisappearingTextPhase.RECALLING -> "Type the sentence you remember."
                DisappearingTextPhase.CORRECT -> "Memory mission cleared!"
                DisappearingTextPhase.WRONG -> "Good try—review the sentence and keep going."
                DisappearingTextPhase.GAME_OVER -> "Memory challenge complete!"
            },
            icon = Icons.Rounded.AutoFixHigh,
            accent = ArcadeColors.Purple,
            onBack = onBack,
            currentStep = state.round.takeIf { state.phase != DisappearingTextPhase.IDLE && state.phase != DisappearingTextPhase.GAME_OVER },
            totalSteps = state.totalRounds.takeIf { state.phase != DisappearingTextPhase.IDLE && state.phase != DisappearingTextPhase.GAME_OVER },
            score = state.score.takeIf { state.phase != DisappearingTextPhase.IDLE },
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
                DisappearingTextPhase.IDLE -> DisappearingTextStartScreen(
                    isLoading = state.isContentLoading,
                    errorMessage = state.errorMessage,
                    onStart = onStart
                )
                DisappearingTextPhase.MEMORIZING,
                DisappearingTextPhase.DISAPPEARING -> MemorizingPhase(state = state)
                DisappearingTextPhase.RECALLING -> RecallingPhase(
                    state = state,
                    onInputChange = onInputChange,
                    onSubmit = onSubmit
                )
                DisappearingTextPhase.CORRECT,
                DisappearingTextPhase.WRONG -> FeedbackPhase(state = state)
                DisappearingTextPhase.GAME_OVER -> GameResultScreen(
                    score = state.score,
                    total = state.totalRounds,
                    xp = state.xpEarned,
                    onPlayAgain = onStart,
                    onBack = onBack,
                    accent = ArcadeColors.Purple
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MemorizingPhase(state: DisappearingTextUiState) {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val semanticColors = rememberGameSemanticColors()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        state.currentQuestion?.let { question ->
            DisappearingTextStoryCard(title = question.sourceTitle)
            Spacer(Modifier.height(spacing.small))
        }

        if (state.phase == DisappearingTextPhase.MEMORIZING || state.phase == DisappearingTextPhase.DISAPPEARING) {
            if (state.isAudioPlaying || state.isAudioLoading) {
                DisappearingTextEqualizerBanner(
                    isAudioLoading = state.isAudioLoading
                )
                Spacer(Modifier.height(spacing.medium))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        tint = ArcadeColors.Purple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(spacing.extraSmall))
                    Text(
                        text = if (state.phase == DisappearingTextPhase.DISAPPEARING) "Words Vanishing!" else "Memorize now",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = ArcadeColors.Purple
                    )
                }

                Spacer(Modifier.height(spacing.small))
                GameTimer(
                    remainingMs = state.readTimerMs,
                    totalMs = state.maxReadTimerMs
                )
                Spacer(Modifier.height(spacing.medium))
            }

            state.errorMessage?.let { error ->
                Spacer(Modifier.height(spacing.extraSmall))
                Text(
                    text = "Audio unavailable: $error\nKeep reading—the game will continue.",
                    style = typography.bodySmall,
                    color = semanticColors.warning,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(spacing.small))
            }
        }

        // Playful Word Count / Memory Power Chip
        val words = state.currentQuestion?.sentence?.split(" ")?.filter { it.isNotBlank() } ?: emptyList()
        val visibleWords = state.visibleWordCount.coerceAtMost(words.size)
        Surface(
            color = ArcadeColors.Purple.copy(alpha = 0.12f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, ArcadeColors.Purple.copy(alpha = 0.35f)),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Psychology,
                    contentDescription = null,
                    tint = ArcadeColors.Purple,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Memory Power: $visibleWords / ${words.size} Words Visible",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ArcadeColors.Purple
                )
            }
        }

        // Playful Sentence Display with 3D Word Blocks
        Surface(
            color = colorScheme.surfaceVariant.copy(alpha = 0.35f),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(
                        ArcadeColors.Purple.copy(alpha = 0.5f),
                        ArcadeColors.Purple.copy(alpha = 0.15f)
                    )
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                words.forEachIndexed { index, word ->
                    val isVisible = index < state.visibleWordCount
                    val isHighlighted = isVisible &&
                        state.isAudioPlaying &&
                        index == state.highlightedWordIndex

                    val blockScale by animateFloatAsState(
                        targetValue = if (isHighlighted) 1.08f else 1f,
                        animationSpec = spring(),
                        label = "word_block_scale_$index"
                    )

                    val blockBackground = when {
                        isHighlighted -> Brush.verticalGradient(
                            listOf(
                                ArcadeColors.Purple.copy(alpha = 0.35f),
                                ArcadeColors.Purple.copy(alpha = 0.15f)
                            )
                        )
                        isVisible -> Brush.verticalGradient(
                            listOf(
                                colorScheme.surface.copy(alpha = 0.9f),
                                colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            )
                        )
                        else -> Brush.verticalGradient(
                            listOf(
                                colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                colorScheme.surfaceVariant.copy(alpha = 0.08f)
                            )
                        )
                    }

                    val blockBorder = when {
                        isHighlighted -> ArcadeColors.Purple
                        isVisible -> ArcadeColors.Purple.copy(alpha = 0.3f)
                        else -> colorScheme.outline.copy(alpha = 0.2f)
                    }

                    Box(
                        modifier = Modifier
                            .scale(blockScale)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(blockBackground)
                            .border(
                                width = if (isHighlighted) 2.dp else 1.dp,
                                color = blockBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isVisible) word else "• • •",
                            style = typography.titleMedium.copy(
                                fontWeight = if (isVisible) FontWeight.ExtraBold else FontWeight.Normal,
                                fontSize = 18.sp
                            ),
                            color = when {
                                isHighlighted -> ArcadeColors.Purple
                                isVisible -> colorScheme.onSurface
                                else -> colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecallingPhase(
    state: DisappearingTextUiState,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val question = state.currentQuestion

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        modifier = Modifier.fillMaxWidth()
    ) {
        question?.let {
            DisappearingTextStoryCard(title = it.sourceTitle)
        }

        // Playful Sentence Builder Word Slot Helper
        if (question != null) {
            SentenceBuilderHelper(
                targetSentence = question.sentence,
                userInput = state.userInput
            )
        }

        // OutlinedTextField (Speed Typer style with label, generous height, clear purple border)
        OutlinedTextField(
            value = state.userInput,
            onValueChange = onInputChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            label = { Text("What did you read?") },
            placeholder = { Text("Type the sentence here...") },
            shape = MaterialTheme.shapes.large,
            minLines = 2,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { if (state.userInput.isNotBlank()) onSubmit() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ArcadeColors.Purple,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(Modifier.height(spacing.small))

        // Playful Primary Action Button (Speed Typer style)
        GamePrimaryButton(
            text = "SUBMIT SENTENCE",
            onClick = onSubmit,
            icon = Icons.Rounded.CheckCircle,
            accent = ArcadeColors.Purple,
            enabled = state.userInput.isNotBlank()
        )
    }
}

/**
 * Playful word counter and slot helper showing children their rebuild progress.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SentenceBuilderHelper(targetSentence: String, userInput: String) {
    val targetWords = targetSentence.split(" ").filter { it.isNotBlank() }
    val userWords = userInput.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    val wordsTypedCount = userWords.size.coerceAtMost(targetWords.size)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = ArcadeColors.Purple.copy(alpha = 0.12f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, ArcadeColors.Purple.copy(alpha = 0.35f)),
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoFixHigh,
                    contentDescription = null,
                    tint = ArcadeColors.Purple,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Sentence Mission: $wordsTypedCount / ${targetWords.size} Words Typed",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ArcadeColors.Purple
                )
            }
        }

        // Mini word bubbles showing progress
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            targetWords.forEachIndexed { index, _ ->
                val isTyped = index < userWords.size
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = 28.dp, height = 10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            if (isTyped) ArcadeColors.Purple else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = 1.dp,
                            color = if (isTyped) ArcadeColors.Purple else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(5.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun DisappearingTextStoryCard(title: String) {
    if (title.isBlank()) return
    Surface(
        color = ArcadeColors.Purple.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ArcadeColors.Purple.copy(alpha = 0.25f)),
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
                    .background(ArcadeColors.Purple.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoStories,
                    contentDescription = null,
                    tint = ArcadeColors.Purple,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "STORY SENTENCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcadeColors.Purple,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun DisappearingTextEqualizerBanner(
    isAudioLoading: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dt_equalizer")
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
                    ArcadeColors.Purple.copy(alpha = 0.7f * pulseAlpha),
                    ArcadeColors.Purple.copy(alpha = 0.2f)
                )
            )
        ),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ArcadeColors.Purple.copy(alpha = 0.35f),
                                ArcadeColors.Purple.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = ArcadeColors.Purple.copy(alpha = 0.85f * pulseAlpha),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Hearing,
                    contentDescription = "Listening",
                    tint = ArcadeColors.Purple,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            DisappearingTextEqualizerWaveform(infiniteTransition = infiniteTransition)

            Spacer(Modifier.height(14.dp))

            Text(
                text = if (isAudioLoading) "Loading Narration..." else "Listen Closely & Memorize...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ArcadeColors.Purple,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Follow along with the words as they are read aloud before they disappear!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun DisappearingTextEqualizerWaveform(infiniteTransition: InfiniteTransition) {
    val barCount = 7
    val barMaxHeights = listOf(18, 34, 24, 44, 28, 38, 20)

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(44.dp)
    ) {
        repeat(barCount) { index ->
            val maxH = barMaxHeights.getOrElse(index) { 26 }
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
                label = "dt_bar_$index"
            )

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height((maxH * barScale + 6).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ArcadeColors.Purple,
                                ArcadeColors.Purple.copy(alpha = 0.45f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun FeedbackPhase(state: DisappearingTextUiState) {
    val isCorrect = state.phase == DisappearingTextPhase.CORRECT
    GameFeedbackBanner(
        title = if (isCorrect) "Awesome memory!" else "Almost there!",
        message = if (isCorrect) {
            "You rebuilt the sentence perfectly."
        } else {
            "The sentence was: “${state.currentQuestion?.sentence.orEmpty()}”"
        },
        type = if (isCorrect) GameFeedbackType.SUCCESS else GameFeedbackType.ENCOURAGEMENT
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Disappearing Text - Memorizing Phase")
@Composable
fun DisappearingTextMemorizingPreview() {
    val sampleQuestion = DisappearingTextQuestion(
        id = "1",
        sentence = "The quick brown fox jumps over the lazy dog",
        sourceTitle = "The Woodland Adventure"
    )

    ESupplementalTheme {
        AppBackground {
            DisappearingTextContent(
                state = DisappearingTextUiState(
                    phase = DisappearingTextPhase.MEMORIZING,
                    currentQuestion = sampleQuestion,
                    visibleWordCount = 5,
                    readTimerMs = 3000L,
                    round = 2,
                    totalRounds = 5,
                    xpEarned = 50
                ),
                onStart = {},
                onBack = {},
                onInputChange = {},
                onSubmit = {}
            )
        }
    }
}

@Preview(name = "Disappearing Text - Recalling Phase")
@Composable
fun DisappearingTextRecallingPreview() {
    val sampleQuestion = DisappearingTextQuestion(
        id = "1",
        sentence = "The quick brown fox jumps over the lazy dog",
        sourceTitle = "The Woodland Adventure"
    )

    ESupplementalTheme {
        AppBackground {
            DisappearingTextContent(
                state = DisappearingTextUiState(
                    phase = DisappearingTextPhase.RECALLING,
                    currentQuestion = sampleQuestion,
                    userInput = "The quick brown fox",
                    round = 3,
                    totalRounds = 5,
                    xpEarned = 100
                ),
                onStart = {},
                onBack = {},
                onInputChange = {},
                onSubmit = {}
            )
        }
    }
}

@Preview(name = "Disappearing Text - Correct Feedback")
@Composable
fun DisappearingTextFeedbackPreview() {
    ESupplementalTheme {
        AppBackground {
            DisappearingTextContent(
                state = DisappearingTextUiState(
                    phase = DisappearingTextPhase.CORRECT,
                    round = 4,
                    totalRounds = 5,
                    xpEarned = 150
                ),
                onStart = {},
                onBack = {},
                onInputChange = {},
                onSubmit = {}
            )
        }
    }
}
