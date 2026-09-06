package com.esupplemental.data.local.repository

import com.esupplemental.data.model.QuizResult

interface QuizRepository {
    suspend fun saveQuizResult(result: QuizResult): Result<Long>
    suspend fun getQuizResult(id: Long): QuizResult?
}