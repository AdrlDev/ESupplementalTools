package com.esupplemental.presentation.state

import com.esupplemental.data.model.SongActivity

data class SongExerciseUiState(
    val mediaId: String = "",
    val activity: SongActivity? = null,
    val fillBlankAnswers: Map<String, String> = emptyMap(),   // itemId -> userAnswer
    val selectedMessageOption: Int = -1,
    val isSubmitted: Boolean = false,
    val isSubmitting: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val submissionError: String? = null,
    val score: Int = 0,
    val total: Int = 0,
    val userAnswersList: List<String> = emptyList(),
    val correctAnswersList: List<String> = emptyList(),
    val resultId: Long? = null
)
