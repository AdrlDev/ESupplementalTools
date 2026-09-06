package com.esupplemental.presentation.state.game_state

import com.esupplemental.data.model.game.GameCatalogue
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.domain.model.game.GameItem

data class GameScreenUiState(
    val selectedDifficulty: GameDifficulty = GameDifficulty.EASY,
    val games: List<GameItem> = GameCatalogue.byDifficulty(GameDifficulty.EASY),
    /** Total XP the student has earned (loaded from repo in a real build). */
    val totalXp: Int = 0,
    /** Games the student has completed at least once. */
    val completedGameIds: Set<String> = setOf()
)