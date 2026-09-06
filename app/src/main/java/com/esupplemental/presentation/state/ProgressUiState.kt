package com.esupplemental.presentation.state

import com.esupplemental.data.local.entity.QuizHistoryEntity
import com.esupplemental.data.model.UserStats
import com.esupplemental.domain.model.game.GameItem

data class ProgressUiState(
    val stats: UserStats = UserStats(),
    val quizScore: Int = 0,
    val quizTotal: Int = 0,
    val userAnswers: List<String> = emptyList(),
    val correctAnswers: List<String> = emptyList(),
    val quizHistory: List<QuizHistoryEntity?> = emptyList(),
    val games: List<GameItem> = emptyList()
)