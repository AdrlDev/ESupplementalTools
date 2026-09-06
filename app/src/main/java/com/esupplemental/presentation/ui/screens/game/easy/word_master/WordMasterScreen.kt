package com.esupplemental.presentation.ui.screens.game.easy.word_master

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.presentation.state.game_state.ListenSlapUiState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.screens.game.GameTopBar
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.ListenSlapPhase
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing
import com.esupplemental.presentation.viewmodel.WordMasterViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

import com.esupplemental.presentation.ui.screens.game.GameResultScreen

private enum class WordMasterContentKey {
    START, PLAYING, GAME_OVER
}

@Composable
fun ListenSlapScreen(
    gameId: String,
    mediaId: String? = null,
    onBack: () -> Unit,
    vm: WordMasterViewModel = koinViewModel(parameters = { parametersOf(gameId, mediaId) })
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    AppBackground {
        WordMasterScreenContent(
            state = state,
            onStartGame = vm::startGame,
            onBack = onBack,
            onTypedWordChange = vm::onTypedWordChange,
            onSubmitAnswer = vm::onSubmitAnswer,
            onReplayWord = vm::replayWord
        )
    }
}

@Composable
fun WordMasterScreenContent(
    state: ListenSlapUiState,
    onStartGame: () -> Unit,
    onBack: () -> Unit,
    onTypedWordChange: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onReplayWord: () -> Unit
) {
    val spacing = MaterialTheme.spacing

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
            ListenSlapPhase.IDLE -> "Hear the word, then type it correctly."
            ListenSlapPhase.GAME_OVER -> "Challenge complete — check your rewards!"
            ListenSlapPhase.SPEAKING -> "Listen closely. Your timer starts next."
            ListenSlapPhase.SELECTING -> "Type the word before time runs out."
            ListenSlapPhase.CORRECT -> "Perfect spelling!"
            ListenSlapPhase.WRONG -> "Good try — the word was ${state.currentWord?.word.orEmpty()}."
        }
        GameTopBar(
            title = "Word Master",
            subtitle = statusText,
            icon = Icons.Rounded.Keyboard,
            accent = ArcadeColors.Navy,
            onBack = onBack,
            currentStep = state.round.takeIf { state.phase != ListenSlapPhase.IDLE && state.phase != ListenSlapPhase.GAME_OVER },
            totalSteps = state.totalRounds.takeIf { state.phase != ListenSlapPhase.IDLE && state.phase != ListenSlapPhase.GAME_OVER },
            score = state.score.takeIf { state.phase != ListenSlapPhase.IDLE },
            xp = state.xpEarned
        )

        Spacer(Modifier.height(spacing.large))

        // --- Animated Screen Transitions ---
        val contentKey = when (state.phase) {
            ListenSlapPhase.IDLE -> WordMasterContentKey.START
            ListenSlapPhase.GAME_OVER -> WordMasterContentKey.GAME_OVER
            else -> WordMasterContentKey.PLAYING
        }

        AnimatedContent(
            targetState = contentKey,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn(tween(350))).togetherWith(
                    slideOutHorizontally { -it } + fadeOut(tween(200))
                )
            },
            label = "word_master_screen"
        ) { key ->
            when (key) {
                WordMasterContentKey.START -> StartScreen(onStart = onStartGame)
                WordMasterContentKey.GAME_OVER -> GameResultScreen(
                    score = state.score,
                    total = state.totalRounds,
                    xp = state.xpEarned,
                    onPlayAgain = onStartGame,
                    onBack = onBack,
                    accent = ArcadeColors.Navy
                )
                WordMasterContentKey.PLAYING -> GamePlayScreen(
                    state = state,
                    onTypedWordChange = onTypedWordChange,
                    onSubmitAnswer = onSubmitAnswer,
                    onReplay = onReplayWord
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WordMasterScreenPreview() {
    ESupplementalTheme {
        AppBackground {
            WordMasterScreenContent(
                state = ListenSlapUiState(),
                onStartGame = {},
                onBack = {},
                onTypedWordChange = {},
                onSubmitAnswer = {},
                onReplayWord = {}
            )
        }
    }
}
