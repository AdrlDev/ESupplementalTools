package com.esupplemental.data.model.game

data class StoryQuestion(
    val id: String,
    val title: String,
    val storyText: String,          // Concise narration sent to the audio API
    val events: List<StoryEvent>,   // Stored in correct order
    val captionParagraphs: List<String> = emptyList(),
    val thumbnailRes: Int? = null,
    val thumbnailUrl: String? = null
)
