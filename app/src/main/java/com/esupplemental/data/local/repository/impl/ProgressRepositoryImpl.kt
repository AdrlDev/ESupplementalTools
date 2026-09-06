package com.esupplemental.data.local.repository.impl

import com.esupplemental.data.local.dao.ProgressDao
import com.esupplemental.data.local.entity.QuizHistoryEntity
import com.esupplemental.data.local.repository.ProgressRepository
import com.esupplemental.data.mapper.DataMapper.toDomainModel
import com.esupplemental.data.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProgressRepositoryImpl(
    private val dao: ProgressDao
) : ProgressRepository {

    override fun getUserStats(userId: String): Flow<UserStats> = dao.getUserStats(userId).map { it.toDomainModel() }

    override fun getQuizHistory(userId: String):Flow<QuizHistoryEntity?> = dao.getQuizHistory(userId).map { it?.firstOrNull() }
    override fun getQuizHistoryList(userId: String): Flow<List<QuizHistoryEntity?>?> = dao.getQuizHistory(userId)
}