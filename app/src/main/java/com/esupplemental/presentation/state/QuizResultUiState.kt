package com.esupplemental.presentation.state

data class QuizResultUiState(
    val isSaving: Boolean = false,
    val error: String? = null,
    val savedSuccessfully: Boolean = false,
    val resultId: Long? = null,
    val score: Int = 0,
    val total: Int = 0,
    val userAnswers: List<String> = emptyList(),
    val correctAnswers: List<String> = emptyList()
)