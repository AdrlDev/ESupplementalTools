package com.esupplemental.data.model

data class QuizResult(
    val score: Int,
    val total: Int,
    val userAnswers: List<String>,
    val correctAnswers: List<String>,
    val type: MediaType
)