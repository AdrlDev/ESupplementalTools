package com.esupplemental.domain.usecases.games

import com.esupplemental.data.local.repository.GameRepository

class SaveGameResultUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(gameId: String, score: Int, totalRounds: Int, xpEarned: Int) {
        repository.saveGameResult(
            gameId = gameId,
            score = score,
            totalRounds = totalRounds,
            xpEarned = xpEarned
        )
    }
}
