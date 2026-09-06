package com.esupplemental.data.local.entity

import androidx.room.*

@Entity(
    tableName = "quiz_history",
    foreignKeys = [
        ForeignKey(
            entity = UserStatsEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val score: Int,
    val total: Int,
    val userAnswers: String, // Stored as CSV or JSON
    val correctAnswers: String,
    val type: String = "SONG",
    val timestamp: Long = System.currentTimeMillis()
)