package com.esupplemental.data.model

import com.esupplemental.data.remote.model.WordTiming

data class AudioStory(
    val id: Int,
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
    val words: List<WordTiming> = emptyList()
)
