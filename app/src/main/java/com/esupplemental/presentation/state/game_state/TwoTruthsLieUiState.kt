package com.esupplemental.presentation.state.game_state

import com.esupplemental.data.model.game.TwoTruthsLieQuestion
import com.esupplemental.presentation.ui.screens.game.moderate.TwoTruthsLiePhase

data class TwoTruthsLieUiState(
    val isContentLoading: Boolean = true,
    val isAudioLoading: Boolean = false,
    val contentError: String? = null,
    val audioError: String? = null,
    val saveError: String? = null,
    val phase: TwoTruthsLiePhase = TwoTruthsLiePhase.IDLE,
    val currentQuestion: TwoTruthsLieQuestion? = null,
    val selectedIndex: Int? = null,
    val isCorrect: Boolean? = null,
    val score: Int = 0,
    val round: Int = 0,
    val totalRounds: Int = 5,
    val xpEarned: Int = 0,
    val isAudioPlaying: Boolean = false
)
