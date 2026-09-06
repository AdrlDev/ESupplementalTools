package com.esupplemental.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.ProgressRepository
import com.esupplemental.domain.usecases.GetLatestQuizResultUseCase
import com.esupplemental.domain.usecases.GetUserProgressUseCase
import com.esupplemental.domain.usecases.games.GetGamesUseCase
import com.esupplemental.domain.utils.UserPreferences
import com.esupplemental.presentation.state.ProgressUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val getUserProgressUseCase: GetUserProgressUseCase,
    private val getLatestQuizResultUseCase: GetLatestQuizResultUseCase,
    private val repository: ProgressRepository,
    private val userPreferences: UserPreferences,
    private val getGamesUseCase: GetGamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        observeProgress()
        observeLatestQuiz()
        observeHistory()
        observeGames()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeGames() {
        viewModelScope.launch {
            userPreferences.userId.flatMapLatest { id ->
                if (id != null) {
                    getGamesUseCase()
                } else {
                    flowOf(emptyList())
                }
            }.collect { gamesList ->
                _uiState.update { it.copy(games = gamesList) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeHistory() {
        viewModelScope.launch {
            userPreferences.userId.flatMapLatest { id ->
                if (id != null) {
                    repository.getQuizHistoryList(id) // Pass id to repository
                } else {
                    flowOf(emptyList())
                }
            }.collect { historyList ->
                _uiState.update { it.copy(quizHistory = historyList ?: emptyList()) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeProgress() {
        viewModelScope.launch {
            userPreferences.userId.flatMapLatest { id ->
                if (id != null) {
                    getUserProgressUseCase(id) // Pass id to use case
                } else {
                    flowOf(null) // Or a default empty progress object
                }
            }.collect { stats ->
                stats?.let { s ->
                    _uiState.update { it.copy(stats = s) }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeLatestQuiz() {
        viewModelScope.launch {
            userPreferences.userId.flatMapLatest { id ->
                if (id != null) {
                    getLatestQuizResultUseCase(id) // Pass id to use case
                } else {
                    flowOf(null)
                }
            }.collect { quiz ->
                quiz?.let { q ->
                    _uiState.update { it.copy(
                        quizScore = q.score,
                        quizTotal = q.total,
                        userAnswers = q.userAnswers.split("|"),
                        correctAnswers = q.correctAnswers.split("|")
                    )}
                }
            }
        }
    }
}
