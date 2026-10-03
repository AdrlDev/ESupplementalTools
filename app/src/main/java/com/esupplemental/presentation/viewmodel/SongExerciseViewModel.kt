package com.esupplemental.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.local.repository.QuizRepository
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.QuizResult
import com.esupplemental.presentation.state.SongExerciseUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SongExerciseViewModel(
    private val repository: MediaRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SongExerciseUiState())
    val uiState: StateFlow<SongExerciseUiState> = _uiState.asStateFlow()

    fun loadActivity(mediaId: String, forceReset: Boolean = false) = viewModelScope.launch {
        val current = _uiState.value
        if (!forceReset && current.mediaId == mediaId && current.activity != null) return@launch

        _uiState.update { it.copy(mediaId = mediaId, loading = true, error = null) }
        runCatching { repository.getSongActivity(mediaId) }
            .onSuccess { activity ->
                _uiState.update {
                    it.copy(
                        mediaId = mediaId,
                        activity = activity,
                        fillBlankAnswers = emptyMap(),
                        selectedMessageOption = -1,
                        isSubmitted = false,
                        isSubmitting = false,
                        score = 0,
                        total = (activity?.fillBlanks?.size ?: 0) + if (activity != null) 1 else 0,
                        userAnswersList = emptyList(),
                        correctAnswersList = emptyList(),
                        resultId = null,
                        loading = false,
                        error = if (activity == null) "This song challenge is unavailable." else null,
                        submissionError = null
                    )
                }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(loading = false, activity = null, error = error.message ?: "Unable to load this challenge.")
                }
            }
    }

    fun onFillBlankAnswerChange(itemId: String, answer: String) {
        if (_uiState.value.isSubmitted || _uiState.value.isSubmitting) return
        _uiState.update { it.copy(fillBlankAnswers = it.fillBlankAnswers + (itemId to answer)) }
    }

    fun onMessageOptionSelected(index: Int) {
        if (_uiState.value.isSubmitted || _uiState.value.isSubmitting) return
        _uiState.update { it.copy(selectedMessageOption = index) }
    }

    fun dismissSubmissionError() = _uiState.update { it.copy(submissionError = null) }

    fun submit() {
        val initial = _uiState.value
        if (initial.activity == null || initial.isSubmitted || initial.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true, submissionError = null) }

        viewModelScope.launch {
            val state = _uiState.value
            val activity = state.activity ?: return@launch
            val userAnswers = mutableListOf<String>()
            val correctAnswers = mutableListOf<String>()
            var score = 0

            activity.fillBlanks.forEachIndexed { index, item ->
                val answer = state.fillBlankAnswers[item.id].orEmpty()
                val correct = normalize(answer) == normalize(item.answer)
                if (correct) score++
                userAnswers += "${index + 1}. ${answer.ifBlank { "No answer" }}"
                correctAnswers += "${index + 1}. ${item.answer}"
            }

            val message = activity.messageQuestion
            val selected = state.selectedMessageOption
            if (selected == message.correctIndex) score++
            userAnswers += "Message: ${message.options.getOrNull(selected) ?: "No answer"}"
            correctAnswers += "Message: ${message.options[message.correctIndex]}"

            val result = QuizResult(score, state.total, userAnswers, correctAnswers, MediaType.SONG)
            quizRepository.saveQuizResult(result)
                .onSuccess { resultId ->
                    _uiState.update {
                        it.copy(isSubmitting = false, isSubmitted = true, score = score, resultId = resultId)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isSubmitting = false, submissionError = error.message ?: "Unable to save your challenge. Please try again.")
                    }
                }
        }
    }

    private fun normalize(value: String): String = value
        .trim()
        .lowercase()
        .replace(Regex("[^\\p{L}\\p{N}']+"), " ")
        .trim()
}
