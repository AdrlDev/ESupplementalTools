package com.esupplemental.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.domain.usecases.games.SaveGameResultUseCase
import com.esupplemental.domain.utils.StoryRecallBank
import com.esupplemental.presentation.state.game_state.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class StoryRecallViewModel(
    application: Application,
    private val repository: MediaRepository,
    private val playbackManager: MediaPlaybackManager,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    private val gameId: String,
    private val initialMediaId: String? = null
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(StoryRecallUiState())
    val uiState = _uiState.asStateFlow()

    private val roundCount = 5
    private var storyQuestions: List<RecallQuestion> = emptyList()
    private var audioJob: Job? = null

    init {
        playbackManager.isPlaybackFinished
            .onEach { finished ->
                if (finished && _uiState.value.phase == StoryRecallPhase.LOADING) {
                    _uiState.update { it.copy(phase = StoryRecallPhase.QUESTION) }
                }
            }
            .launchIn(viewModelScope)

        loadStory()
    }

    private fun loadStory() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        val mediaId = initialMediaId ?: repository.getMediaList(MediaType.STORY).first()
            .randomOrNull()?.id
            
        if (mediaId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "No stories available.") }
            return@launch
        }

        val context = repository.getStoryContext(mediaId)
        if (context == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Could not load story context.") }
            return@launch
        }

        // Initialize bank from story assets
        val allStories = repository.getMediaList(MediaType.STORY).first()
        StoryRecallBank.init(getApplication(), allStories)
        storyQuestions = StoryRecallBank.getQuestions(mediaId)

        _uiState.update { 
            it.copy(
                storyContext = context,
                isLoading = false,
                totalRounds = roundCount.coerceAtMost(storyQuestions.size),
                phase = StoryRecallPhase.IDLE
            ) 
        }
    }

    fun startGame() {
        if (_uiState.value.isLoading || storyQuestions.isEmpty()) return
        loadNextRound(resetGame = true)
    }

    private fun loadNextRound(resetGame: Boolean = false) {
        val state = _uiState.value
        val nextRound = if (resetGame) 1 else state.round + 1

        if (nextRound > roundCount || nextRound > storyQuestions.size) {
            finishGame()
            return
        }

        val question = storyQuestions[nextRound - 1]

        _uiState.update {
            it.copy(
                phase = StoryRecallPhase.LOADING,
                currentQuestion = question,
                round = nextRound,
                score = if (resetGame) 0 else it.score,
                selectedChoice = -1,
                errorMessage = null
            )
        }

        playQuestionAudio(question)
    }

    private fun playQuestionAudio(question: RecallQuestion) {
        audioJob?.cancel()
        audioJob = viewModelScope.launch {
            repository.generateAudioForText(
                id = question.id,
                title = "Story Recall",
                text = question.question
            ).onSuccess { audioStory ->
                val mediaItem = MediaItem(
                    id = question.id,
                    title = "Question",
                    type = MediaType.STORY,
                    durationSeconds = audioStory.durationSeconds,
                    audioUrl = audioStory.url
                )
                playbackManager.prepare(mediaItem, emptyList(), emptyList())
                playbackManager.togglePlayPause()
            }.onFailure {
                // Fallback
                delay(3000.milliseconds)
                _uiState.update { it.copy(phase = StoryRecallPhase.QUESTION) }
            }
        }
    }

    fun replayQuestion() {
        val question = _uiState.value.currentQuestion ?: return
        playQuestionAudio(question)
    }

    fun onChoiceSelected(index: Int) {
        if (_uiState.value.phase != StoryRecallPhase.QUESTION) return
        _uiState.update { it.copy(selectedChoice = index) }
        submitAnswer()
    }

    private fun submitAnswer() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val isCorrect = state.selectedChoice == question.correctIndex
        
        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.update { 
            it.copy(
                phase = if (isCorrect) StoryRecallPhase.CORRECT else StoryRecallPhase.WRONG,
                score = newScore
            ) 
        }

        viewModelScope.launch {
            delay(2000.milliseconds)
            loadNextRound()
        }
    }

    private fun finishGame() {
        val state = _uiState.value
        val xp = (state.score.toFloat() / state.totalRounds * 40).toInt().coerceAtLeast(10)

        _uiState.update { it.copy(phase = StoryRecallPhase.GAME_OVER, xpEarned = xp) }

        viewModelScope.launch {
            saveGameResultUseCase(
                gameId = gameId,
                score = state.score,
                totalRounds = state.totalRounds,
                xpEarned = xp
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioJob?.cancel()
        playbackManager.release()
    }
}
