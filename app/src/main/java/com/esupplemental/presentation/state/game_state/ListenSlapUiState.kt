package com.esupplemental.presentation.state.game_state

import com.esupplemental.data.model.game.WordItem
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.ListenSlapPhase

data class ListenSlapUiState(
    val phase: ListenSlapPhase = ListenSlapPhase.IDLE,
    val currentWord: WordItem? = null,
    val typedWord: String = "",
    val options: List<WordItem> = emptyList(),
    val selectedOptionId: String? = null,
    val score: Int = 0,
    val round: Int = 1,
    val totalRounds: Int = 10,
    val timerMs: Long = 15_000L,
    val maxTimerMs: Long = 15_000L,
    val xpEarned: Int = 0
)
