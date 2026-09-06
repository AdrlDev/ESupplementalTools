package com.esupplemental.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.domain.usecases.games.SaveGameResultUseCase
import com.esupplemental.presentation.state.game_state.ListenSlapUiState
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.ListenSlapPhase
import com.esupplemental.presentation.ui.screens.game.easy.word_master.WordMasterBank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class WordMasterViewModel(
    application: Application,
    private val repository: MediaRepository,
    private val playbackManager: MediaPlaybackManager,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    private val gameId: String,
    private val mediaId: String? = null
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ListenSlapUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Initialize Word Bank dynamically from assets in background
        viewModelScope.launch(Dispatchers.IO) {
            WordMasterBank.init(application)
        }

        // Observe playback status to unlock UI when audio finishes
        playbackManager.isPlaying
            .onEach { isPlaying ->
                val state = _uiState.value
                if (state.phase == ListenSlapPhase.SPEAKING && !isPlaying) {
                    unlockOptionsManually()
                }
            }
            .launchIn(viewModelScope)

        playbackManager.error
            .onEach { error ->
                if (error != null) {
                    unlockOptionsManually()
                }
            }
            .launchIn(viewModelScope)
    }

    // Helper to centrally manage unlocking the game
    private fun unlockOptionsManually() {
        if (_uiState.value.phase == ListenSlapPhase.SPEAKING) {
            _uiState.update { it.copy(phase = ListenSlapPhase.SELECTING) }
            startTimer()
        }
    }

    // ── Game flow ─────────────────────────────────────────────────────────────

    fun startGame() {
        loadNextRound(resetGame = true)
    }

    private fun loadNextRound(resetGame: Boolean = false) {
        val word = WordMasterBank.random(mediaId)

        _uiState.update { current ->
            if (resetGame) {
                ListenSlapUiState(
                    phase = ListenSlapPhase.SPEAKING,
                    currentWord = word,
                    typedWord = "",
                    timerMs = current.maxTimerMs
                )
            } else {
                current.copy(
                    currentWord = word,
                    typedWord = "",
                    selectedOptionId = null,
                    phase = ListenSlapPhase.SPEAKING,
                    timerMs = current.maxTimerMs
                )
            }
        }
        trySpeakCurrentWord()
    }

    private fun trySpeakCurrentWord() = viewModelScope.launch {
        val state = _uiState.value
        if (state.phase != ListenSlapPhase.SPEAKING) return@launch
        val wordItem = state.currentWord ?: return@launch

        val result = repository.generateAudioForText(wordItem.id, null, wordItem.word)
        
        result.onSuccess { audioStory ->
            val playbackMedia = MediaItem(
                id = wordItem.id,
                title = wordItem.word,
                type = MediaType.STORY,
                durationSeconds = 0,
                audioUrl = audioStory.url
            )
            playbackManager.prepare(playbackMedia, emptyList())
            playbackManager.togglePlayPause()
        }.onFailure {
            // If generation fails, skip to selecting so user can at least try
            unlockOptionsManually()
        }
    }

    fun onTypedWordChange(text: String) {
        if (_uiState.value.phase != ListenSlapPhase.SELECTING) return
        _uiState.update { it.copy(typedWord = text) }
    }

    fun replayWord() {
        val state = _uiState.value
        if (state.phase != ListenSlapPhase.SELECTING) return

        timerJob?.cancel()
        _uiState.update {
            it.copy(phase = ListenSlapPhase.SPEAKING, timerMs = it.maxTimerMs)
        }
        trySpeakCurrentWord()
    }

    // ── Timer ─────────────────────────────────────────────────────────────────

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val tick = 50L
            while (_uiState.value.timerMs > 0 &&
                _uiState.value.phase == ListenSlapPhase.SELECTING
            ) {
                delay(tick.milliseconds)
                _uiState.update { it.copy(timerMs = (it.timerMs - tick).coerceAtLeast(0)) }
            }
            if (_uiState.value.phase == ListenSlapPhase.SELECTING) {
                resolveAnswer(timedOut = true)
            }
        }
    }

    // ── Answer handling ───────────────────────────────────────────────────────

    fun onSubmitAnswer() {
        if (_uiState.value.phase != ListenSlapPhase.SELECTING) return
        timerJob?.cancel()
        resolveAnswer(timedOut = false)
    }

    private fun resolveAnswer(timedOut: Boolean) {
        val state = _uiState.value
        val correctWord = state.currentWord?.word ?: ""
        val isCorrect = !timedOut && state.typedWord.trim().equals(correctWord, ignoreCase = true)
        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.update {
            it.copy(
                phase = if (isCorrect) ListenSlapPhase.CORRECT else ListenSlapPhase.WRONG,
                score = newScore
            )
        }

        viewModelScope.launch {
            delay(1200L.milliseconds)
            advanceRound()
        }
    }

    private fun advanceRound() {
        val state = _uiState.value
        if (state.round >= state.totalRounds) {
            val xp = (state.score.toFloat() / state.totalRounds * 10).toInt()

            _uiState.update { it.copy(phase = ListenSlapPhase.GAME_OVER, xpEarned = xp) }

            viewModelScope.launch {
                saveGameResultUseCase(
                    gameId = gameId,
                    score = state.score,
                    totalRounds = state.totalRounds,
                    xpEarned = xp
                )
            }
        } else {
            _uiState.update { it.copy(round = it.round + 1) }
            loadNextRound()
        }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        playbackManager.release()
    }
}
