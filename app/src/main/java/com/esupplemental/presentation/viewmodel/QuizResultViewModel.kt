package com.esupplemental.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.QuizRepository
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.QuizResult
import com.esupplemental.presentation.state.QuizResultUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizResultViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizResultUiState())
    val uiState = _uiState.asStateFlow()

    fun loadResult(resultId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = quizRepository.getQuizResult(resultId)
            if (result != null) {
                _uiState.update { it.copy(
                    isSaving = false,
                    score = result.score,
                    total = result.total,
                    userAnswers = result.userAnswers,
                    correctAnswers = result.correctAnswers,
                    resultId = resultId
                )}
            } else {
                _uiState.update { it.copy(isSaving = false, error = "Result not found") }
            }
        }
    }

    fun submitResults(score: Int, total: Int, userAnswers: List<String>, correctAnswers: List<String>, type: MediaType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val result = QuizResult(score, total, userAnswers, correctAnswers, type)
            val response = quizRepository.saveQuizResult(result)

            response.onSuccess { id ->
                _uiState.update { it.copy(isSaving = false, savedSuccessfully = true, resultId = id) }
            }.onFailure { error ->
                _uiState.update { it.copy(isSaving = false, error = error.message) }
            }
        }
    }
}
