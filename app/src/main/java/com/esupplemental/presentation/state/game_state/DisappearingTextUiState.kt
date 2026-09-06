package com.esupplemental.presentation.state.game_state

import com.esupplemental.data.model.game.DisappearingTextQuestion
import com.esupplemental.presentation.ui.screens.game.moderate.DisappearingTextPhase

data class DisappearingTextUiState(
    val isContentLoading: Boolean = true,
    val errorMessage: String? = null,
    val phase: DisappearingTextPhase = DisappearingTextPhase.IDLE,
    val currentQuestion: DisappearingTextQuestion? = null,
    val visibleWordCount: Int = 0,
    val highlightedWordIndex: Int = -1,
    val isAudioLoading: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val userInput: String = "",
    val score: Int = 0,
    val round: Int = 0,
    val totalRounds: Int = 5,
    val xpEarned: Int = 0,
    val readTimerMs: Long = 5_000L,
    val maxReadTimerMs: Long = 5_000L
)
