package com.esupplemental.presentation.state.game_state

import com.esupplemental.data.model.game.MinimalPairQuestion
import com.esupplemental.presentation.ui.screens.game.moderate.MinimalPairsPhase

data class MinimalPairsUiState(
    val isContentLoading: Boolean = true,
    val isAudioLoading: Boolean = false,
    val errorMessage: String? = null,
    val phase: MinimalPairsPhase = MinimalPairsPhase.IDLE,
    val currentQuestion: MinimalPairQuestion? = null,
    val selectedWord: String? = null,
    val isCorrect: Boolean? = null,
    val score: Int = 0,
    val round: Int = 0,
    val totalRounds: Int = 8,
    val xpEarned: Int = 0,
    val isAudioPlaying: Boolean = false
)
