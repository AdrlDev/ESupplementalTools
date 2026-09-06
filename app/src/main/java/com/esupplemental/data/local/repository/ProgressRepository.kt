package com.esupplemental.data.local.repository

import com.esupplemental.data.local.entity.QuizHistoryEntity
import com.esupplemental.data.model.UserStats
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getUserStats(userId: String): Flow<UserStats>
    fun getQuizHistory(userId: String): Flow<QuizHistoryEntity?>
    fun getQuizHistoryList(userId: String): Flow<List<QuizHistoryEntity?>?>
}