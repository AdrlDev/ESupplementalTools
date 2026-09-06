package com.esupplemental.data.model

data class MultipleChoiceItem(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)