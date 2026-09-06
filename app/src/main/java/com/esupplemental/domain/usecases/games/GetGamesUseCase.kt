package com.esupplemental.domain.usecases.games

import com.esupplemental.data.local.repository.GameRepository
import com.esupplemental.domain.model.game.GameDifficulty
import com.esupplemental.domain.model.game.GameItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetGamesUseCase(private val repository: GameRepository) {

    // Returns all games filtered by difficulty
    operator fun invoke(difficulty: GameDifficulty? = null): Flow<List<GameItem>> {
        return repository.getGames().map { list ->
            if (difficulty == null) list
            else list.filter { it.difficulty == difficulty }
        }
    }
}