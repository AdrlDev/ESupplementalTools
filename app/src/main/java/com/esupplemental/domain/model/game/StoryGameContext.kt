package com.esupplemental.domain.model.game

import com.esupplemental.data.remote.model.WordTiming

/**
 * Reusable domain model for story context needed by games.
 */
data class StoryGameContext(
    val mediaId: String,
    val title: String,
    val transcript: String,
    val sentences: List<String>,
    val words: List<String>,
    val wordTimings: List<WordTiming>,
    val audioUrl: String?,
    val durationSeconds: Int
)
