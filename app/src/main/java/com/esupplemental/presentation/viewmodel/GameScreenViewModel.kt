package com.esupplemental.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.esupplemental.BuildConfig
import com.esupplemental.presentation.state.game_state.GameScreenUiState
import com.esupplemental.domain.model.game.GameDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import androidx.lifecycle.viewModelScope
import com.esupplemental.domain.usecases.games.GetGamesUseCase
import com.esupplemental.domain.usecases.games.SyncInitialGamesUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GameScreenViewModel(
    private val getGamesUseCase: GetGamesUseCase,
    private val syncInitialGamesUseCase: SyncInitialGamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameScreenUiState())
    val uiState: StateFlow<GameScreenUiState> = _uiState.asStateFlow()

    init {
        prepareData()
        viewModelScope.launch {
            combine(getGamesUseCase(), _uiState) { allGames, state ->
                allGames to state.selectedDifficulty
            }.collectLatest { (allGames, difficulty) ->
                val totalXp = allGames.sumOf { it.xpReward } // or however you store earned XP
                val completedIds = allGames.filter { it.stars > 0 }.map { it.id }.toSet()
                val easyIds = setOf("listen_slap", "story_order", "character_quest")
                val moderateIds = setOf("disappearing_text", "two_truths_lie", "minimal_pairs")
                val hardIds = setOf("speed_typer", "follow_directions", "story_recall")
                val supportedIds = easyIds + moderateIds + hardIds
                val allEasyCompleted = allGames
                    .filter { it.id in easyIds }
                    .let { games -> games.size == easyIds.size && games.all { it.stars > 0 } }
                val allModerateCompleted = allGames
                    .filter { it.id in moderateIds }
                    .let { games -> games.size == moderateIds.size && games.all { it.stars > 0 } }
                val bypassProgression = BuildConfig.ALLOW_GAME_PROGRESSION_BYPASS
                val tierUnlocked = bypassProgression || when (difficulty) {
                    GameDifficulty.EASY -> true
                    GameDifficulty.MODERATE -> allEasyCompleted
                    GameDifficulty.HARD -> allModerateCompleted
                }
                val gamesForDifficulty = allGames.filter { it.difficulty == difficulty }
                val games = gamesForDifficulty.mapIndexed { index, game ->
                    val previousCompleted = index == 0 ||
                        gamesForDifficulty.getOrNull(index - 1)?.stars?.let { it > 0 } == true
                    game.copy(
                        isLocked = game.id !in supportedIds ||
                            (!bypassProgression && (!tierUnlocked || !previousCompleted))
                    )
                }
                _uiState.update {
                    it.copy(games = games, totalXp = totalXp, completedGameIds = completedIds)
                }
            }
        }
    }

    private fun prepareData() {
        viewModelScope.launch {
            // 1. Seed the DB if it's the first time
            syncInitialGamesUseCase()
        }
    }

    fun selectDifficulty(difficulty: GameDifficulty) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }
}
