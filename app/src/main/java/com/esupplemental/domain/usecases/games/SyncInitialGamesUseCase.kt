package com.esupplemental.domain.usecases.games

import com.esupplemental.data.local.repository.GameRepository

/**
 * UseCase to seed the local database with the hardcoded GameCatalogue.
 * This ensures that even on the first run, the user sees the list of games.
 */
class SyncInitialGamesUseCase(private val repository: GameRepository) {
    suspend operator fun invoke() {
        repository.refreshCatalogue()
    }
}