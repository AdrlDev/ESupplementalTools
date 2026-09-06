package com.esupplemental.data.model.game

data class BingoWord(
    val id: String,
    val word: String,
    val emoji: String,
    val sourceLabel: String = "",
    val isStory: Boolean = true,
    val isMarked: Boolean = false
)