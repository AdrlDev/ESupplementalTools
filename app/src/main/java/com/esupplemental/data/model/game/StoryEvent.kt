package com.esupplemental.data.model.game

data class StoryEvent(
    val id: String,
    val description: String,
    val correctOrder: Int           // 1-based
)