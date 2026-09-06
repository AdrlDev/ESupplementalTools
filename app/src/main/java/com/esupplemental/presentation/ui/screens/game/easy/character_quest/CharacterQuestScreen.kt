package com.esupplemental.presentation.ui.screens.game.easy.character_quest

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.domain.utils.CharacterOption
import com.esupplemental.domain.utils.CharacterQuestQuestion
import com.esupplemental.presentation.state.game_state.CharacterQuestUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.screens.game.GameLoadingState
import com.esupplemental.presentation.ui.screens.game.GameLockedState
import com.esupplemental.presentation.ui.screens.game.GamePrimaryButton
import com.esupplemental.presentation.ui.screens.game.GameResultScreen
import com.esupplemental.presentation.ui.screens.game.GameSecondaryButton
import com.esupplemental.presentation.ui.screens.game.GameTopBar
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.CharacterQuestPhase
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.CharacterQuestViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CharacterQuestScreen(
    gameId: String,
    mediaId: String? = null,
    onBack: () -> Unit,
    viewModel: CharacterQuestViewModel = koinViewModel(parameters = { parametersOf(gameId, mediaId) })
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AppBackground {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GameLoadingState("Preparing character quest…", accent = ArcadeColors.Teal)
            }
            state.errorMessage != null && state.currentQuestion == null -> Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameLockedState(state.errorMessage ?: "Character quest is unavailable.")
                GamePrimaryButton("TRY AGAIN", viewModel::loadGame, accent = ArcadeColors.Teal)
                Spacer(Modifier.height(8.dp))
                GameSecondaryButton("BACK TO GAMES", onBack, accent = ArcadeColors.Teal)
            }
            state.phase == CharacterQuestPhase.GAME_OVER -> {
                GameResultScreen(
                    score = state.score,
                    total = state.totalQuestions,
                    xp = state.xpEarned,
                    onPlayAgain = viewModel::replayGame,
                    onBack = onBack,
                    accent = ArcadeColors.Teal
                )
            }
            else -> {
                CharacterQuestContent(
                    state = state,
                    onPlayAudio = viewModel::playAudio,
                    onSelectCharacter = viewModel::selectCharacter,
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
fun CharacterQuestContent(
    state: CharacterQuestUiState,
    onPlayAudio: () -> Unit,
    onSelectCharacter: (Int) -> Unit,
    onBack: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val currentQ = state.currentQuestion ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.screenPadding)
            .padding(bottom = spacing.large)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(52.dp))

        GameTopBar(
            title = "Character Quest",
            subtitle = "Who is speaking or described in this story quote?",
            icon = Icons.Rounded.RecordVoiceOver,
            accent = ArcadeColors.Teal,
            onBack = onBack,
            currentStep = state.questionIndex + 1,
            totalSteps = state.totalQuestions,
            score = state.score.takeIf { it > 0 },
            xp = state.xpEarned
        )

        Spacer(Modifier.height(spacing.medium))

        @SuppressLint("UnusedContentLambdaTargetStateParameter")
        AnimatedContent(
            targetState = state.questionIndex,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn(tween(300))).togetherWith(
                    slideOutHorizontally { -it } + fadeOut(tween(200))
                )
            },
            label = "question_transition"
        ) { _ ->
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Speech Quote Card
                CharacterQuoteBubble(
                    question = currentQ,
                    isPlaying = state.isAudioPlaying,
                    isLoading = state.isAudioLoading,
                    onPlayAudio = onPlayAudio
                )

                // Explanation Banner when answered
                AnimatedVisibility(
                    visible = state.phase == CharacterQuestPhase.FEEDBACK,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val isCorrect = state.isCorrect == true
                    val bannerColor = if (isCorrect) ArcadeColors.Success else ArcadeColors.Rose

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = bannerColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.5.dp, bannerColor.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Close,
                                contentDescription = null,
                                tint = bannerColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (isCorrect) "Awesome! That's correct! 🎉 (+15 XP)" else "Nice try! Here's the match:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                    color = bannerColor
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = currentQ.explanation,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Character Cards Options List
                Text(
                    text = "TAP THE MATCHING CHARACTER:",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = ArcadeColors.Teal
                )

                currentQ.characters.forEachIndexed { index, character ->
                    val cardState = when {
                        state.phase == CharacterQuestPhase.FEEDBACK && index == currentQ.correctIndex -> {
                            if (state.selectedCharacterIndex == index) CharacterCardState.CORRECT
                            else CharacterCardState.REVEALED_CORRECT
                        }
                        state.phase == CharacterQuestPhase.FEEDBACK && state.selectedCharacterIndex == index -> {
                            CharacterCardState.WRONG
                        }
                        state.selectedCharacterIndex == index -> CharacterCardState.SELECTED
                        else -> CharacterCardState.IDLE
                    }

                    CharacterCard(
                        character = character,
                        cardState = cardState,
                        enabled = state.phase != CharacterQuestPhase.FEEDBACK,
                        onClick = { onSelectCharacter(index) }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Character Quest - Listening Phase")
@Composable
fun CharacterQuestListeningPreview() {
    val sampleQuestion = CharacterQuestQuestion(
        id = "preview_1",
        storyTitle = "Black Beauty",
        contextClue = "From the beloved tale of a faithful horse:",
        quoteOrTrait = "\"I was born a handsome black colt with a white star on my forehead. I always try my best to be gentle and brave.\"",
        audioText = "I was born a handsome black colt with a white star on my forehead. Who am I?",
        characters = listOf(
            CharacterOption("Black Beauty", "The Noble Horse", "🐴", 0xFF0B2E63),
            CharacterOption("Farmer Grey", "The Kind Master", "👨‍🌾", 0xFF047857),
            CharacterOption("Ginger", "The Feisty Mare", "🐎", 0xFFB45309),
            CharacterOption("John Manly", "The Trusted Coachman", "🎩", 0xFF7C3AED)
        ),
        correctIndex = 0,
        explanation = "Black Beauty is the courageous horse with a white star who narrates the story!"
    )

    val sampleState = CharacterQuestUiState(
        isLoading = false,
        phase = CharacterQuestPhase.LISTENING,
        questions = listOf(sampleQuestion),
        questionIndex = 0,
        totalQuestions = 5,
        currentQuestion = sampleQuestion,
        isAudioPlaying = true,
        score = 2,
        xpEarned = 30
    )

    ESupplementalTheme(darkTheme = false) {
        AppBackground {
            CharacterQuestContent(
                state = sampleState,
                onPlayAudio = {},
                onSelectCharacter = {},
                onBack = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Character Quest - Correct Feedback (Night)")
@Composable
fun CharacterQuestFeedbackNightPreview() {
    val sampleQuestion = CharacterQuestQuestion(
        id = "preview_2",
        storyTitle = "The Prince and the Pauper",
        contextClue = "From the royal switch in London:",
        quoteOrTrait = "\"Born in Offal Court dressed in rags, I dreamed of palaces and kings until one day I traded clothes with the royal prince!\"",
        audioText = "Born in Offal Court dressed in rags, I dreamed of palaces and kings until one day I traded clothes with the royal prince! Who am I?",
        characters = listOf(
            CharacterOption("Prince Edward", "Prince of Wales", "👑", 0xFFE3A008),
            CharacterOption("Tom Canty", "The Dreamer Pauper", "👦", 0xFF0797A5),
            CharacterOption("Miles Hendon", "The Brave Knight", "⚔️", 0xFFBE123C),
            CharacterOption("King Henry VIII", "The Royal King", "🏰", 0xFF6B21A8)
        ),
        correctIndex = 1,
        explanation = "Tom Canty is the poor boy who traded places with Prince Edward and found himself living in the palace!"
    )

    val sampleState = CharacterQuestUiState(
        isLoading = false,
        phase = CharacterQuestPhase.FEEDBACK,
        questions = listOf(sampleQuestion),
        questionIndex = 1,
        totalQuestions = 5,
        currentQuestion = sampleQuestion,
        selectedCharacterIndex = 1,
        isCorrect = true,
        score = 3,
        xpEarned = 45
    )

    ESupplementalTheme(darkTheme = true) {
        AppBackground {
            CharacterQuestContent(
                state = sampleState,
                onPlayAudio = {},
                onSelectCharacter = {},
                onBack = {}
            )
        }
    }
}

