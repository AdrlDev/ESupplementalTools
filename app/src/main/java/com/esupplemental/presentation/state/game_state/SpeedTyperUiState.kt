package com.esupplemental.presentation.state.game_state

import androidx.compose.runtime.Immutable
import com.esupplemental.domain.model.game.StoryGameContext

@Immutable
data class SpeedTyperUiState(
    val phase: SpeedTyperPhase = SpeedTyperPhase.IDLE,
    val storyContext: StoryGameContext? = null,
    val currentPhrase: String = "",
    val userInput: String = "",
    val round: Int = 0,
    val totalRounds: Int = 3,
    val score: Int = 0,
    val xpEarned: Int = 0,
    val isAudioLoading: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val timerMs: Long = 0,
    val maxTimerMs: Long = 15000,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

enum class SpeedTyperPhase {
    IDLE,
    LOADING,
    LISTENING,
    TYPING,
    CORRECT,
    WRONG,
    GAME_OVER
}
