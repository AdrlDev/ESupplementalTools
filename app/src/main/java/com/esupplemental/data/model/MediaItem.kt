package com.esupplemental.data.model

data class MediaItem(
    val id: String,
    val title: String,
    val singer: String? = null,
    val category: StoryCategory? = null,           // e.g. "Moral: Kindness"
    val type: MediaType,
    val durationSeconds: Int,
    val thumbnailRes: Int? = null,
    val thumbnailUrl: String? = null,
    val audioRes: Int? = null,
    val audioUrl: String? = null,
    val transcript: String = "",
    val moral: String? = null
)
