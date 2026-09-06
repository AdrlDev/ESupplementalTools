package com.esupplemental.domain.usecases

import com.esupplemental.data.local.repository.ProgressRepository
import com.esupplemental.data.model.UserStats
import kotlinx.coroutines.flow.Flow

class GetUserProgressUseCase(private val repository: ProgressRepository) {
    operator fun invoke(userId: String): Flow<UserStats> = repository.getUserStats(userId)
}