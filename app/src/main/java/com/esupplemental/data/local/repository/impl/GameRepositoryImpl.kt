package com.esupplemental.data.local.repository.impl

import com.esupplemental.data.local.dao.GameDao
import com.esupplemental.data.model.game.GameCatalogue
import com.esupplemental.data.local.repository.GameRepository
import com.esupplemental.data.mapper.DataMapper.toDomain
import com.esupplemental.data.mapper.DataMapper.toEntity
import com.esupplemental.domain.model.game.GameItem
import com.esupplemental.domain.utils.UserPreferences
import com.esupplemental.domain.utils.GameScoring
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class GameRepositoryImpl(
    private val gameDao: GameDao,
    private val userPreferences: UserPreferences
) : GameRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getGames(): Flow<List<GameItem>> =
        userPreferences.userId.flatMapLatest { userId ->
            if (userId == null) emptyFlow()
            else gameDao.getAllGames(userId).map { entities -> entities.map { it.toDomain() } }
        }

    override suspend fun refreshCatalogue() {
        val userId = userPreferences.userId.firstOrNull() ?: return
        val entities = GameCatalogue.all.map { it.toEntity(userId) }
        gameDao.insertGames(entities)
    }

    override suspend fun unlockGame(gameId: String) {
        val userId = userPreferences.userId.firstOrNull() ?: return
        gameDao.updateLockStatus(userId, gameId, isLocked = false)
    }

    override suspend fun saveGameResult(
        gameId: String,
        score: Int,
        totalRounds: Int,
        xpEarned: Int
    ) {
        if (totalRounds <= 0) return
        val userId = userPreferences.userId.firstOrNull() ?: return
        val stars = GameScoring.stars(score, totalRounds)

        // Update the specific game in Room
        gameDao.updateGameProgress(
            userId = userId,
            gameId = gameId,
            stars = stars,
            xpEarned = xpEarned.coerceAtLeast(0),
            isLocked = false
        )
    }
}
