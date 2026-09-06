package com.esupplemental.data.model

data class UserStats(
    val userName: String = "Alex",
    val level: Int = 2,
    val levelProgress: Float = 0.75f,       // 0f..1f
    val songsCompleted: Int = 4,
    val storiesCompleted: Int = 3,
    val quizzesAverage: Float = 0.85f,
    val overallScore: Float = 0.85f
)