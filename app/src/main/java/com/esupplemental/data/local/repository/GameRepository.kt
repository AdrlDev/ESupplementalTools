package com.esupplemental.data.local.repository

import com.esupplemental.domain.model.game.GameItem
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getGames(): Flow<List<GameItem>>
    suspend fun refreshCatalogue() // Syncs local DB with GameCatalogue object
    suspend fun unlockGame(gameId: String)
    suspend fun saveGameResult(gameId: String, score: Int, totalRounds: Int, xpEarned: Int)
}
