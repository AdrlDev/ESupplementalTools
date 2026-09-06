package com.esupplemental.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audio_stories",
    indices = [Index(value = ["mediaId"])]
)
data class AudioStoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mediaId: String,
    val remoteId: Int,
    val title: String,
    val url: String,
    val fileName: String,
    val voiceId: String,
    val modelId: String,
    val characterCount: Int,
    val fileSize: Long,
    val durationSeconds: Int,
    val remoteCreatedAt: String,
    val wordsJson: String? = null
)
