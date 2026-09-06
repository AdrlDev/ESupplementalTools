package com.esupplemental.data.model.game

data class WordItem(
    val id: String,
    val word: String,
    val emoji: String,
    val sourceTitle: String = "",
    val mediaId: String = ""
)
