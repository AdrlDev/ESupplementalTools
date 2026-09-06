package com.esupplemental.data.local.dao

import androidx.room.*
import com.esupplemental.data.local.entity.QuizHistoryEntity
import com.esupplemental.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_stats WHERE userId = :userId LIMIT 1")
    fun getUserStats(userId: String): Flow<UserStatsEntity>

    @Query("SELECT * FROM quiz_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getQuizHistory(userId: String): Flow<List<QuizHistoryEntity?>?>
}