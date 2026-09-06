package com.esupplemental.data.local.entity

import androidx.room.*

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String,
    val userName: String,
    val level: Int = 1,
    val levelProgress: Float = 0f,
    val songsCompleted: Int = 0,
    val storiesCompleted: Int = 0,
    val quizzesAverage: Float = 0f,
    val overallScore: Float = 0f
)