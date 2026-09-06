package com.esupplemental.presentation.ui.screens.game.easy.story_order

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.data.model.game.StoryQuestion
import com.esupplemental.presentation.state.game_state.StoryOrderUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.GameLoadingState
import com.esupplemental.presentation.ui.screens.game.GameLockedState
import com.esupplemental.presentation.ui.screens.game.GamePrimaryButton
import com.esupplemental.presentation.ui.screens.game.GameSecondaryButton
import com.esupplemental.presentation.ui.screens.game.GameTopBar
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.StoryOrderPhase
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.StoryOrderViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

import com.esupplemental.presentation.ui.screens.game.GameResultScreen

@Composable
fun StoryOrderScreen(
    gameId: String,
    mediaId: String? = null,
    onBack: () -> Unit,
    viewModel: StoryOrderViewModel = koinViewModel(parameters = { parametersOf(gameId, mediaId) })
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AppBackground {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GameLoadingState("Loading your story quest", accent = ArcadeColors.Teal)
            }
            state.errorMessage != null -> Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameLockedState(state.errorMessage ?: "The story quest could not be loaded yet.")
                GamePrimaryButton("TRY AGAIN", viewModel::retryLoading, accent = ArcadeColors.Teal)
                Spacer(Modifier.height(8.dp))
                GameSecondaryButton("BACK TO GAMES", onBack, accent = ArcadeColors.Teal)
            }
            else -> StoryOrderScreenContent(
                state = state,
                onBack = onBack,
                onPlayStory = viewModel::playStory,
                onStartOrdering = viewModel::startOrdering,
                onReOrderEvents = viewModel::reorderEvents,
                onCheckOrder = viewModel::checkOrder,
                onReplayGame = viewModel::replayGame
            )
        }
    }
}

@Composable
fun StoryOrderScreenContent(
    state: StoryOrderUiState,
    onBack: () -> Unit,
    onPlayStory: () -> Unit,
    onStartOrdering: () -> Unit,
    onReOrderEvents: (Int, Int) -> Unit,
    onCheckOrder: () -> Unit,
    onReplayGame: () -> Unit
) {
    val spacing = MaterialTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.screenPadding)
            .padding(bottom = spacing.large)
    ) {
        Spacer(Modifier.height(52.dp))

        GameTopBar(
            title = "Story Order",
            subtitle = when (state.phase) {
                StoryOrderPhase.LISTENING -> "Listen first, then rebuild the story."
                StoryOrderPhase.ORDERING -> "Drag each event into the right sequence."
                StoryOrderPhase.CHECKING -> "Checking your story path…"
                StoryOrderPhase.GAME_OVER -> "All story quests complete!"
            },
            icon = Icons.Rounded.AutoStories,
            accent = ArcadeColors.Teal,
            onBack = onBack,
            currentStep = (state.questionIndex + 1).takeIf { state.phase != StoryOrderPhase.GAME_OVER },
            totalSteps = state.totalQuestions.takeIf { state.phase != StoryOrderPhase.GAME_OVER },
            score = state.totalCorrect.takeIf { it > 0 },
            xp = state.xpEarned
        )

        Spacer(Modifier.height(spacing.medium))

        AnimatedContent(
            targetState = state.phase,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn(tween(350))).togetherWith(
                    slideOutHorizontally { -it } + fadeOut(tween(200))
                )
            },
            label = "phase_transition"
        ) { phase ->
            when (phase) {
                StoryOrderPhase.LISTENING -> ListeningPhase(
                    state = state,
                    onPlayStory = onPlayStory,
                    onStartOrdering = onStartOrdering
                )

                StoryOrderPhase.ORDERING,
                StoryOrderPhase.CHECKING -> OrderingPhase(
                    state = state,
                    onReOrderEvents = onReOrderEvents,
                    onCheckOrder = onCheckOrder
                )

                StoryOrderPhase.GAME_OVER -> GameResultScreen(
                    score = state.totalCorrect,
                    total = state.totalPossibleEvents.coerceAtLeast(state.totalQuestions),
                    xp = state.xpEarned,
                    onPlayAgain = onReplayGame,
                    onBack = onBack,
                    accent = ArcadeColors.Teal
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Listening Phase")
@Composable
fun StoryOrderScreenListeningPreview() {
    ESupplementalTheme {
        AppBackground {
            StoryOrderScreenContent(
                state = StoryOrderUiState(
                    phase = StoryOrderPhase.LISTENING,
                    questionIndex = 1,
                    totalQuestions = 5,
                    xpEarned = 50,
                    currentStory = StoryQuestion(
                        id = "sample",
                        title = "The Lost Puppy",
                        storyText = "",
                        events = emptyList()
                    )
                ),
                onBack = {},
                onPlayStory = {},
                onStartOrdering = {},
                onReOrderEvents = { _, _ -> },
                onCheckOrder = {},
                onReplayGame = {}
            )
        }
    }
}
