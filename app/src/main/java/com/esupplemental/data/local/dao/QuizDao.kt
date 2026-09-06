package com.esupplemental.data.local.dao

import androidx.room.*
import com.esupplemental.data.local.entity.*

@Dao
interface QuizDao {
    @Insert
    suspend fun insertQuizHistory(history: QuizHistoryEntity): Long

    @Query("SELECT * FROM quiz_history WHERE id = :id")
    suspend fun getQuizHistoryById(id: Long): QuizHistoryEntity?

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    suspend fun getUserStats(userId: String): UserStatsEntity?

    @Update
    suspend fun updateUserStats(stats: UserStatsEntity)

    @Query("SELECT COUNT(*) FROM quiz_history WHERE userId = :userId")
    suspend fun getQuizCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM quiz_history WHERE userId = :userId AND type = :type")
    suspend fun getQuizCountByType(userId: String, type: String): Int

    @Query("SELECT COALESCE(SUM(score), 0) FROM quiz_history WHERE userId = :userId")
    suspend fun getTotalQuizScore(userId: String): Int

    @Query(
        """SELECT COALESCE(AVG(
            CASE WHEN total > 0 THEN CAST(score AS REAL) / total ELSE 0 END
        ), 0) FROM quiz_history WHERE userId = :userId"""
    )
    suspend fun getQuizAverage(userId: String): Float

    @Transaction
    suspend fun saveQuizResultAndUpdateStats(history: QuizHistoryEntity): Long {
        val currentStats = getUserStats(history.userId) ?: error("Stats not found")
        val id = insertQuizHistory(history)
        val completedQuizzes = getQuizCount(history.userId)
        val quizzesPerLevel = 20
        val updatedStats = currentStats.copy(
            level = 1 + (completedQuizzes / quizzesPerLevel),
            levelProgress = (completedQuizzes % quizzesPerLevel) / quizzesPerLevel.toFloat(),
            songsCompleted = getQuizCountByType(history.userId, "SONG"),
            storiesCompleted = getQuizCountByType(history.userId, "STORY"),
            quizzesAverage = getQuizAverage(history.userId).coerceIn(0f, 1f),
            overallScore = getTotalQuizScore(history.userId).toFloat()
        )
        updateUserStats(updatedStats)
        return id
    }
}
