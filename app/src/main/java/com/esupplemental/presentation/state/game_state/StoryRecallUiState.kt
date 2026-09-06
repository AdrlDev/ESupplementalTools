package com.esupplemental.presentation.state.game_state

import androidx.compose.runtime.Immutable
import com.esupplemental.domain.model.game.StoryGameContext

@Immutable
data class StoryRecallUiState(
    val phase: StoryRecallPhase = StoryRecallPhase.IDLE,
    val storyContext: StoryGameContext? = null,
    val currentQuestion: RecallQuestion? = null,
    val round: Int = 0,
    val totalRounds: Int = 3,
    val score: Int = 0,
    val xpEarned: Int = 0,
    val selectedChoice: Int = -1,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

enum class StoryRecallPhase {
    IDLE,
    LOADING,
    QUESTION,
    CORRECT,
    WRONG,
    GAME_OVER
}

data class RecallQuestion(
    val id: String,
    val question: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String? = null
)
