package com.esupplemental.data.model

data class Note(
    val id: String,
    val title: String,
    val mainIdea: String = "",
    val keyDetails: String = "",
    val summary: String = "",
    val keywords: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)