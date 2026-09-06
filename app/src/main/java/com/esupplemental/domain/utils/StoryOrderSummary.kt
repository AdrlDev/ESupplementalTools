package com.esupplemental.domain.utils

import com.esupplemental.data.model.game.StoryEvent

/**
 * Builds the concise narration shared by Story Order prefetching and playback.
 * Keeping the cache key and text here prevents the game from requesting a
 * different MP3 than the one generated in the background.
 */
object StoryOrderSummary {
    fun audioId(storyId: String, narration: String): String =
        AudioCacheKey.fromText("${storyId}_story_order_summary", narration)

    fun audioTitle(storyTitle: String): String = "$storyTitle — Short Story"

    fun captionParagraphs(events: List<StoryEvent>): List<String> =
        events
            .asSequence()
            .sortedBy { it.correctOrder }
            .map { it.description.trim() }
            .filter { it.isNotBlank() }
            .chunked(2)
            .map { sentences -> sentences.joinToString(" ") }
            .toList()

    fun narration(events: List<StoryEvent>): String =
        captionParagraphs(events).joinToString("\n\n")
}
