package com.esupplemental.data.local.entity

import androidx.room.*

@Entity(
    tableName = "notes",
    indices = [Index(value = ["userId", "createdAt"])]
)
data class NoteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val mainIdea: String,
    val keyDetails: String,
    val summary: String,
    val keywords: List<String>,
    val createdAt: Long = System.currentTimeMillis()
)
