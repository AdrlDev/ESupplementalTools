package com.esupplemental.presentation.ui.screens.game.easy.story_order

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.esupplemental.presentation.state.game_state.StoryOrderUiState
import com.esupplemental.presentation.ui.screens.game.GameResultScreen
import com.esupplemental.presentation.ui.theme.ArcadeColors
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun GameOverPhase(
    state: StoryOrderUiState,
    onReplayGame: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    GameResultScreen(
        score = state.totalCorrect,
        total = state.totalPossibleEvents,
        xp = state.xpEarned,
        onPlayAgain = onReplayGame,
        onBack = onBack,
        modifier = modifier.verticalScroll(rememberScrollState()),
        title = "Story quest complete!",
        accent = ArcadeColors.Teal
    )
}

@Preview(showBackground = true)
@Composable
private fun StoryOrderResultPreview() {
    ESupplementalTheme(dynamicColor = false) {
        GameOverPhase(
            state = StoryOrderUiState(totalCorrect = 18, totalPossibleEvents = 20, xpEarned = 15),
            onReplayGame = {},
            onBack = {}
        )
    }
}
