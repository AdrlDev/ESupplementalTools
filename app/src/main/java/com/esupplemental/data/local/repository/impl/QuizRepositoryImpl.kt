package com.esupplemental.data.local.repository.impl

import com.esupplemental.data.local.dao.QuizDao
import com.esupplemental.data.local.entity.QuizHistoryEntity
import com.esupplemental.data.local.repository.QuizRepository
import com.esupplemental.data.model.QuizResult
import com.esupplemental.domain.utils.UserPreferences
import kotlinx.coroutines.flow.first

class QuizRepositoryImpl(
    private val quizDao: QuizDao,
    private val userPrefs: UserPreferences
) : QuizRepository {

    override suspend fun saveQuizResult(result: QuizResult): Result<Long> = runCatching {
        // 1. Get User ID from DataStore flow
        val userId = userPrefs.userId.first() ?: throw Exception("User not logged in")

        require(result.total > 0) { "Quiz total must be greater than zero" }
        require(result.score in 0..result.total) { "Quiz score must be between zero and total" }

        // Stats are recalculated from history inside the same Room transaction.
        val historyEntity = QuizHistoryEntity(
            userId = userId,
            score = result.score,
            total = result.total,
            userAnswers = result.userAnswers.joinToString("|"),
            correctAnswers = result.correctAnswers.joinToString("|"),
            type = result.type.name
        )

        quizDao.saveQuizResultAndUpdateStats(historyEntity)
    }

    override suspend fun getQuizResult(id: Long): QuizResult? {
        val entity = quizDao.getQuizHistoryById(id) ?: return null
        return QuizResult(
            score = entity.score,
            total = entity.total,
            userAnswers = entity.userAnswers.split("|"),
            correctAnswers = entity.correctAnswers.split("|"),
            type = com.esupplemental.data.model.MediaType.valueOf(entity.type)
        )
    }
}
