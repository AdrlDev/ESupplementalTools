package com.esupplemental.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.esupplemental.data.local.entity.GameItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE userId = :userId")
    fun getAllGames(userId: String): Flow<List<GameItemEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGames(games: List<GameItemEntity>)

    @Query("UPDATE games SET isLocked = :isLocked WHERE userId = :userId AND id = :gameId")
    suspend fun updateLockStatus(userId: String, gameId: String, isLocked: Boolean)

    @Query("UPDATE games SET stars = MAX(stars, :stars), xpReward = MAX(xpReward, :xpEarned), isLocked = :isLocked WHERE userId = :userId AND id = :gameId")
    suspend fun updateGameProgress(userId: String, gameId: String, stars: Int, xpEarned: Int, isLocked: Boolean)
}
