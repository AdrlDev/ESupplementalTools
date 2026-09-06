package com.esupplemental.data.model

data class MultipleChoiceQuestion(
    val id: String,
    val question: String,
    val choices: List<String>,
    val correctAnswerIndex: Int
)