package com.esupplemental.domain.usecases

import com.esupplemental.data.local.entity.QuizHistoryEntity
import com.esupplemental.data.local.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow

class GetLatestQuizResultUseCase(private val repository: ProgressRepository) {
    operator fun invoke(userId: String): Flow<QuizHistoryEntity?> = repository.getQuizHistory(userId)
}