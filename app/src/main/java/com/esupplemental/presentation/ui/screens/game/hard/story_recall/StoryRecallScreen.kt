package com.esupplemental.presentation.ui.screens.game.hard.story_recall

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.presentation.state.game_state.*
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.*
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.StoryRecallViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.ui.tooling.preview.Preview
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun StoryRecallScreen(
    gameId: String,
    mediaId: String? = null,
    onBack: () -> Unit,
    viewModel: StoryRecallViewModel = koinViewModel(parameters = { parametersOf(gameId, mediaId) })
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = MaterialTheme.spacing

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.screenPadding)
                .padding(bottom = spacing.large)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            val statusText = when (state.phase) {
                StoryRecallPhase.IDLE -> "Test your memory and understanding of the story!"
                StoryRecallPhase.LOADING -> "Preparing your questions..."
                StoryRecallPhase.QUESTION -> "Read carefully and choose the best answer."
                StoryRecallPhase.CORRECT -> "Exactly right! You have a great memory."
                StoryRecallPhase.WRONG -> "Not quite. Review the story carefully next time!"
                StoryRecallPhase.GAME_OVER -> "Challenge complete! You're a story expert."
            }

            GameTopBar(
                title = "Story Recall Challenge",
                subtitle = statusText,
                icon = Icons.Rounded.Psychology,
                accent = ArcadeColors.Navy,
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
                label = "story_recall_content"
            ) { phase ->
                when (phase) {
                    StoryRecallPhase.IDLE -> {
                        GameStartContent(onStart = viewModel::startGame)
                    }
                    StoryRecallPhase.LOADING -> {
                        RecallListeningContent()
                    }
                    StoryRecallPhase.QUESTION, StoryRecallPhase.CORRECT, StoryRecallPhase.WRONG -> {
                        QuestionContent(
                            state = state,
                            onChoiceSelected = viewModel::onChoiceSelected,
                            onReplay = viewModel::replayQuestion
                        )
                    }
                    StoryRecallPhase.GAME_OVER -> {
                        GameResultScreen(
                            score = state.score,
                            total = state.totalRounds,
                            xp = state.xpEarned,
                            onPlayAgain = viewModel::startGame,
                            onBack = onBack,
                            accent = ArcadeColors.Navy
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecallListeningContent() {
    val spacing = MaterialTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition(label = "recall_listening_equalizer")

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
                        ArcadeColors.Navy.copy(alpha = 0.7f * pulseAlpha),
                        colorScheme.primary.copy(alpha = 0.3f)
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
                        contentDescription = "Listening to question",
                        tint = ArcadeColors.Navy,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Animated Equalizer Waveform
                RecallEqualizerWaveform(infiniteTransition = infiniteTransition)

                Spacer(Modifier.height(24.dp))

                // Title
                Text(
                    text = "Listen to Question...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ArcadeColors.Navy,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                // Reminder Sub-text
                Text(
                    text = "Listen carefully to the question! The answer choices will appear as soon as the narration finishes.",
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
private fun RecallEqualizerWaveform(infiniteTransition: InfiniteTransition) {
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
                label = "recall_eq_bar_$index"
            )

            val barBrush = if (index % 2 == 0) {
                Brush.verticalGradient(
                    colors = listOf(
                        ArcadeColors.Navy,
                        ArcadeColors.Navy.copy(alpha = 0.55f)
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
private fun GameStartContent(onStart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(24.dp)
    ) {
        Text(
            "Ready for a Brain Teaser?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Answer detailed questions about the characters and events you encountered in the story. Good luck!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        GamePrimaryButton(
            text = "START CHALLENGE",
            onClick = onStart,
            icon = Icons.Rounded.PlayArrow,
            accent = ArcadeColors.Navy
        )
    }
}

@Composable
private fun QuestionContent(
    state: StoryRecallUiState,
    onChoiceSelected: (Int) -> Unit,
    onReplay: () -> Unit
) {
    val question = state.currentQuestion ?: return
    val story = state.storyContext
    val spacing = MaterialTheme.spacing
    
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
                        Icon(Icons.Rounded.Psychology, null, modifier = Modifier.align(Alignment.Center), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "STORY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(text = story.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question.question,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = onReplay) {
                    Icon(Icons.Rounded.Replay, "Replay Audio")
                }
            }
        }

        Spacer(Modifier.height(spacing.large))

        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            modifier = Modifier.fillMaxWidth()
        ) {
            question.choices.forEachIndexed { index, choice ->
                val answerState = when {
                    state.selectedChoice == index && state.phase == StoryRecallPhase.CORRECT -> GameAnswerState.CORRECT
                    state.selectedChoice == index && state.phase == StoryRecallPhase.WRONG -> GameAnswerState.INCORRECT
                    state.selectedChoice == index -> GameAnswerState.SELECTED
                    state.phase == StoryRecallPhase.WRONG && index == question.correctIndex -> GameAnswerState.CORRECT
                    else -> GameAnswerState.DEFAULT
                }

                GameAnswerOption(
                    label = ('A' + index).toString(),
                    text = choice,
                    state = answerState,
                    onClick = { onChoiceSelected(index) },
                    enabled = state.phase == StoryRecallPhase.QUESTION,
                    accent = ArcadeColors.Navy
                )
            }
        }

        if (state.phase == StoryRecallPhase.CORRECT || state.phase == StoryRecallPhase.WRONG) {
            Spacer(Modifier.height(spacing.large))
            GameFeedbackBanner(
                title = if (state.phase == StoryRecallPhase.CORRECT) "Correct!" else "Not quite!",
                message = question.explanation ?: (if (state.phase == StoryRecallPhase.CORRECT) "You remembered perfectly." else "The correct answer was ${('A' + question.correctIndex)}."),
                type = if (state.phase == StoryRecallPhase.CORRECT) GameFeedbackType.SUCCESS else GameFeedbackType.ENCOURAGEMENT
            )
        }
    }
}

@Preview(
    name = "Story Recall - Start",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun StoryRecallStartPreview() {
    ESupplementalTheme {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameTopBar(
                    title = "Story Recall Challenge",
                    subtitle = "Test your memory and understanding of the story!",
                    icon = Icons.Rounded.Psychology,
                    accent = ArcadeColors.Navy,
                    onBack = {},
                    currentStep = null,
                    totalSteps = null,
                    score = 0,
                    xp = 0
                )

                Spacer(Modifier.height(32.dp))

                GameStartContent(
                    onStart = {}
                )
            }
        }
    }
}

@Preview(
    name = "Story Recall - Question",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun StoryRecallQuestionPreview() {
    ESupplementalTheme {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameTopBar(
                    title = "Story Recall Challenge",
                    subtitle = "Read carefully and choose the best answer.",
                    icon = Icons.Rounded.Psychology,
                    accent = ArcadeColors.Navy,
                    onBack = {},
                    currentStep = 2,
                    totalSteps = 5,
                    score = 1,
                    xp = 5
                )

                Spacer(Modifier.height(24.dp))

                QuestionContent(
                    state = StoryRecallUiState(
                        phase = StoryRecallPhase.QUESTION,
                        round = 2,
                        totalRounds = 5,
                        score = 1,
                        xpEarned = 5,
                        selectedChoice = 0,
                        currentQuestion = RecallQuestion(
                            id = "q1",
                            question = "Why did the main character decide to continue despite the difficulty?",
                            choices = listOf(
                                "They wanted to prove they were stronger than everyone else.",
                                "They remembered the goal they were trying to achieve.",
                                "Someone forced them to continue.",
                                "They accidentally chose the wrong path."
                            ),
                            correctIndex = 1,
                            explanation = "The character continued because the goal was important enough to keep trying."
                        )
                    ),
                    onChoiceSelected = {},
                    onReplay = {}
                )
            }
        }
    }
}

@Preview(
    name = "Story Recall - Correct Answer",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun StoryRecallCorrectPreview() {
    ESupplementalTheme {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameTopBar(
                    title = "Story Recall Challenge",
                    subtitle = "Exactly right! You have a great memory.",
                    icon = Icons.Rounded.Psychology,
                    accent = ArcadeColors.Navy,
                    onBack = {},
                    currentStep = 3,
                    totalSteps = 5,
                    score = 3,
                    xp = 15
                )

                Spacer(Modifier.height(24.dp))

                QuestionContent(
                    state = StoryRecallUiState(
                        phase = StoryRecallPhase.CORRECT,
                        round = 3,
                        totalRounds = 5,
                        score = 3,
                        xpEarned = 15,
                        selectedChoice = 1,
                        currentQuestion = RecallQuestion(
                            id = "q2",
                            question = "Which event best explains why the character changed their decision?",
                            choices = listOf(
                                "A friend left the village.",
                                "They discovered an important clue.",
                                "The weather suddenly changed.",
                                "They forgot what they were doing."
                            ),
                            correctIndex = 1,
                            explanation = "Finding the clue gave the character new information and changed what they decided to do next."
                        )
                    ),
                    onChoiceSelected = {},
                    onReplay = {}
                )
            }
        }
    }
}

@Preview(
    name = "Story Recall - Wrong Answer",
    showBackground = true,
    widthDp = 420,
    heightDp = 820
)
@Composable
private fun StoryRecallWrongPreview() {
    ESupplementalTheme {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameTopBar(
                    title = "Story Recall Challenge",
                    subtitle = "Not quite. Review the story carefully next time!",
                    icon = Icons.Rounded.Psychology,
                    accent = ArcadeColors.Navy,
                    onBack = {},
                    currentStep = 4,
                    totalSteps = 5,
                    score = 2,
                    xp = 10
                )

                Spacer(Modifier.height(24.dp))

                QuestionContent(
                    state = StoryRecallUiState(
                        phase = StoryRecallPhase.WRONG,
                        round = 4,
                        totalRounds = 5,
                        score = 2,
                        xpEarned = 10,
                        selectedChoice = 0,
                        currentQuestion = RecallQuestion(
                            id = "q3",
                            question = "What happened immediately before the character reached the destination?",
                            choices = listOf(
                                "They returned home.",
                                "They followed the final clue.",
                                "They started the journey.",
                                "They met a new character."
                            ),
                            correctIndex = 1,
                            explanation = "The final clue led directly to the destination."
                        )
                    ),
                    onChoiceSelected = {},
                    onReplay = {}
                )
            }
        }
    }
}
