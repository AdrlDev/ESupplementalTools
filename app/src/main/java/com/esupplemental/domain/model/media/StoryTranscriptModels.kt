package com.esupplemental.domain.model.media

/**
 * Represents a single speakable chunk of the transcript (e.g., a sentence).
 */
data class StoryChunk(
    val index: Int,
    val text: String,
    val startWordIndex: Int,
    val endWordIndex: Int
)

/**
 * Represents a logical page in the story book, containing one or more chunks.
 */
data class StoryPage(
    val index: Int,
    val chunks: List<StoryChunk>
)
