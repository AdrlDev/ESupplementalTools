package com.esupplemental.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.domain.usecases.games.SaveGameResultUseCase
import com.esupplemental.presentation.state.game_state.SpeedTyperPhase
import com.esupplemental.presentation.state.game_state.SpeedTyperUiState
import com.esupplemental.domain.utils.SpeedTyperBank
import com.esupplemental.domain.utils.SpeedTyperChallenge
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SpeedTyperViewModel(
    application: Application,
    private val repository: MediaRepository,
    private val playbackManager: MediaPlaybackManager,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    private val gameId: String,
    private val initialMediaId: String? = null
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SpeedTyperUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var gameStartTime: Long = 0
    private var availableChallenges: List<SpeedTyperChallenge> = emptyList()
    private val roundCount = 4
    private var isSubmitting = false

    init {
        playbackManager.isPlaybackFinished
            .onEach { finished ->
                if (finished && _uiState.value.phase == SpeedTyperPhase.LISTENING) {
                    startTypingPhase()
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
        SpeedTyperBank.init(getApplication(), allStories)

        _uiState.update { 
            it.copy(
                storyContext = context,
                isLoading = false,
                totalRounds = roundCount,
                phase = SpeedTyperPhase.IDLE
            ) 
        }
    }

    fun startGame() {
        if (_uiState.value.isLoading || _uiState.value.storyContext == null) return
        loadNextRound(resetGame = true)
    }

    private fun loadNextRound(resetGame: Boolean = false) {
        val state = _uiState.value
        val context = state.storyContext ?: return
        val nextRound = if (resetGame) 1 else state.round + 1

        if (nextRound > roundCount) {
            finishGame()
            return
        }

        val challenges = SpeedTyperBank.getChallenges(context.mediaId)
        val challenge = challenges.getOrNull(nextRound - 1) ?: challenges.randomOrNull() ?: return
        isSubmitting = false

        _uiState.update {
            it.copy(
                phase = SpeedTyperPhase.LOADING,
                currentPhrase = challenge.sentence,
                userInput = "",
                round = nextRound,
                score = if (resetGame) 0 else it.score,
                timerMs = it.maxTimerMs,
                errorMessage = null
            )
        }

        playExcerptAudio(challenge)
    }

    private fun playExcerptAudio(challenge: SpeedTyperChallenge) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAudioPlaying = true, phase = SpeedTyperPhase.LISTENING) }
            
            repository.generateAudioForText(
                id = challenge.id,
                title = "Speed Typer",
                text = challenge.sentence
            ).onSuccess { audioStory ->
                val mediaItem = MediaItem(
                    id = challenge.id,
                    title = "Excerpt",
                    type = MediaType.STORY,
                    durationSeconds = audioStory.durationSeconds,
                    audioUrl = audioStory.url
                )
                playbackManager.prepare(mediaItem, emptyList(), emptyList())
                playbackManager.togglePlayPause()
            }.onFailure { error ->
                // Fallback directly to typing if audio fails
                startTypingPhase()
            }
        }
    }

    private fun startTypingPhase() {
        _uiState.update { it.copy(phase = SpeedTyperPhase.TYPING, isAudioPlaying = false, timerMs = it.maxTimerMs) }
        gameStartTime = System.currentTimeMillis()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerMs > 0 && _uiState.value.phase == SpeedTyperPhase.TYPING) {
                delay(100.milliseconds)
                _uiState.update { it.copy(timerMs = (it.timerMs - 100).coerceAtLeast(0)) }
            }
            if (_uiState.value.phase == SpeedTyperPhase.TYPING) {
                submitAnswer()
            }
        }
    }

    fun onUserInputChange(input: String) {
        _uiState.update { it.copy(userInput = input) }
    }

    fun submitAnswer() {
        val state = _uiState.value
        if (state.phase != SpeedTyperPhase.TYPING || isSubmitting) return
        isSubmitting = true

        timerJob?.cancel()
        val original = state.currentPhrase.trim().lowercase()
            .replace(Regex("[^a-z0-9\\s]"), "")
            .replace(Regex("\\s+"), " ")
        val userTyped = state.userInput.trim().lowercase()
            .replace(Regex("[^a-z0-9\\s]"), "")
            .replace(Regex("\\s+"), " ")

        val isCorrect = original == userTyped
        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.update { 
            it.copy(
                phase = if (isCorrect) SpeedTyperPhase.CORRECT else SpeedTyperPhase.WRONG,
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
        val xp = (state.score.toFloat() / roundCount * 35).toInt().coerceAtLeast(5)

        _uiState.update { it.copy(phase = SpeedTyperPhase.GAME_OVER, xpEarned = xp) }

        viewModelScope.launch {
            saveGameResultUseCase(
                gameId = gameId,
                score = state.score,
                totalRounds = roundCount,
                xpEarned = xp
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        playbackManager.release()
    }
}
