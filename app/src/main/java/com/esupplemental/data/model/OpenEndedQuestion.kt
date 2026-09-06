package com.esupplemental.data.model

data class OpenEndedQuestion(
    val id: String,
    val question: String,
    val sampleAnswer: String = ""
)