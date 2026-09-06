package com.esupplemental.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.game.TwoTruthsLieQuestion
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.domain.usecases.games.SaveGameResultUseCase
import com.esupplemental.domain.utils.TwoTruthsLieBank
import com.esupplemental.presentation.state.game_state.TwoTruthsLieUiState
import com.esupplemental.presentation.ui.screens.game.moderate.TwoTruthsLiePhase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TwoTruthsLieViewModel(
    application: Application,
    private val repository: MediaRepository,
    private val playbackManager: MediaPlaybackManager,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    private val gameId: String,
    private val mediaId: String? = null
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TwoTruthsLieUiState())
    val uiState = _uiState.asStateFlow()

    private var availableQuestions: List<TwoTruthsLieQuestion> = emptyList()
    private var roundQuestions: List<TwoTruthsLieQuestion> = emptyList()
    private var audioJob: Job? = null
    private var roundTransitionJob: Job? = null
    private var gameSessionId = 0

    init {
        playbackManager.isPlaying
            .onEach { playing ->
                _uiState.update { state ->
                    if (state.phase != TwoTruthsLiePhase.LISTENING) state
                    else state.copy(
                        isAudioPlaying = playing,
                        isAudioLoading = if (playing) false else state.isAudioLoading
                    )
                }
            }
            .launchIn(viewModelScope)

        playbackManager.isBuffering
            .onEach { buffering ->
                if (buffering && _uiState.value.phase == TwoTruthsLiePhase.LISTENING) {
                    _uiState.update { it.copy(isAudioLoading = true) }
                }
            }
            .launchIn(viewModelScope)

        // Ignore the singleton player's current value from a previous screen.
        playbackManager.isPlaybackFinished
            .drop(1)
            .onEach { finished ->
                if (finished) unlockSelection()
            }
            .launchIn(viewModelScope)

        playbackManager.error
            .drop(1)
            .onEach { error ->
                if (error != null) {
                    _uiState.update {
                        it.copy(
                            audioError = error,
                            isAudioLoading = false,
                            isAudioPlaying = false
                        )
                    }
                    unlockSelection()
                }
            }
            .launchIn(viewModelScope)

        loadQuestions(startWhenReady = false)
    }

    private fun loadQuestions(startWhenReady: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val result = runCatching { TwoTruthsLieBank.load(getApplication<Application>()) }

        result.onSuccess { questions ->
            availableQuestions = questions
            _uiState.update {
                it.copy(
                    isContentLoading = false,
                    contentError = if (questions.size < ROUND_COUNT) {
                        "Not enough suitable statements were found in the story assets."
                    } else {
                        null
                    }
                )
            }
            if (startWhenReady && questions.size >= ROUND_COUNT) startGame()
        }.onFailure { error ->
            _uiState.update {
                it.copy(
                    isContentLoading = false,
                    contentError = error.message ?: "Unable to load story statements."
                )
            }
        }
    }

    fun startGame() {
        if (_uiState.value.isContentLoading) return
        if (availableQuestions.size < ROUND_COUNT) {
            _uiState.update { it.copy(isContentLoading = true, contentError = null) }
            loadQuestions(startWhenReady = true)
            return
        }

        roundTransitionJob?.cancel()
        gameSessionId += 1
        roundQuestions = if (!mediaId.isNullOrBlank()) {
            val prioritized = availableQuestions.filter { it.id.startsWith("${mediaId}_") }
            val remainder = availableQuestions.filter { !it.id.startsWith("${mediaId}_") }.shuffled()
            (prioritized + remainder).take(ROUND_COUNT)
        } else {
            availableQuestions.shuffled().take(ROUND_COUNT)
        }
        _uiState.update { it.copy(totalRounds = roundQuestions.size) }
        loadNextRound(resetGame = true)
    }

    private fun loadNextRound(resetGame: Boolean = false) {
        val nextRound = if (resetGame) 1 else _uiState.value.round + 1
        val question = roundQuestions.getOrNull(nextRound - 1) ?: return

        audioJob?.cancel()
        playbackManager.stop()
        _uiState.update {
            it.copy(
                phase = TwoTruthsLiePhase.LISTENING,
                currentQuestion = question.copy(statements = question.statements.shuffled()),
                selectedIndex = null,
                isCorrect = null,
                round = nextRound,
                score = if (resetGame) 0 else it.score,
                xpEarned = if (resetGame) 0 else it.xpEarned,
                isAudioLoading = true,
                isAudioPlaying = false,
                audioError = null,
                saveError = null
            )
        }

        playAudio()
    }

    fun playAudio() {
        val state = _uiState.value
        if (
            state.phase != TwoTruthsLiePhase.LISTENING &&
            state.phase != TwoTruthsLiePhase.SELECTING
        ) return
        if (state.isAudioPlaying) return
        val question = state.currentQuestion ?: return

        audioJob?.cancel()
        playbackManager.stop()
        _uiState.update {
            it.copy(
                phase = TwoTruthsLiePhase.LISTENING,
                selectedIndex = null,
                isCorrect = null,
                isAudioLoading = true,
                isAudioPlaying = false,
                audioError = null
            )
        }

        audioJob = viewModelScope.launch {
            repository.generateAudioForText(
                id = question.id,
                title = TwoTruthsLieBank.audioTitle(question),
                text = question.audioText
            ).onSuccess { audioStory ->
                if (_uiState.value.currentQuestion?.id != question.id) return@onSuccess

                val playbackMedia = MediaItem(
                    id = question.id,
                    title = TwoTruthsLieBank.audioTitle(question),
                    type = MediaType.STORY,
                    durationSeconds = audioStory.durationSeconds,
                    audioUrl = audioStory.url
                )
                playbackManager.prepare(playbackMedia, emptyList())
                playbackManager.togglePlayPause()
            }.onFailure { error ->
                unlockSelection(error.message ?: "Unable to play the story context.")
            }
        }
    }

    private fun unlockSelection(audioError: String? = null) {
        if (_uiState.value.phase != TwoTruthsLiePhase.LISTENING) return
        _uiState.update {
            it.copy(
                phase = TwoTruthsLiePhase.SELECTING,
                isAudioLoading = false,
                isAudioPlaying = false,
                audioError = audioError
            )
        }
    }

    fun onStatementSelected(index: Int) {
        if (_uiState.value.phase != TwoTruthsLiePhase.SELECTING) return
        _uiState.update { it.copy(selectedIndex = index) }
    }

    fun submitAnswer() {
        val state = _uiState.value
        if (state.phase != TwoTruthsLiePhase.SELECTING) return
        val selectedIndex = state.selectedIndex ?: return
        val selectedStatement = state.currentQuestion?.statements?.getOrNull(selectedIndex) ?: return
        val isLie = !selectedStatement.isTrue
        val newScore = if (isLie) state.score + 1 else state.score

        _uiState.update {
            it.copy(
                phase = if (isLie) TwoTruthsLiePhase.CORRECT else TwoTruthsLiePhase.WRONG,
                isCorrect = isLie,
                score = newScore,
                isAudioLoading = false,
                isAudioPlaying = false,
                audioError = null
            )
        }

        roundTransitionJob?.cancel()
        roundTransitionJob = viewModelScope.launch {
            delay(FEEDBACK_DURATION_MS)
            if (state.round >= state.totalRounds) {
                finishGame()
            } else {
                loadNextRound()
            }
        }
    }

    private fun finishGame() {
        playbackManager.stop()
        val state = _uiState.value
        val completedSessionId = gameSessionId
        val xp = (state.score.toFloat() / state.totalRounds.coerceAtLeast(1) * MAX_XP).toInt()

        _uiState.update {
            it.copy(
                phase = TwoTruthsLiePhase.GAME_OVER,
                xpEarned = xp,
                isAudioLoading = false,
                isAudioPlaying = false,
                audioError = null,
                saveError = null
            )
        }

        viewModelScope.launch {
            runCatching {
                saveGameResultUseCase(
                    gameId = gameId,
                    score = state.score,
                    totalRounds = state.totalRounds,
                    xpEarned = xp
                )
            }.onFailure { error ->
                _uiState.update {
                    if (
                        gameSessionId != completedSessionId ||
                        it.phase != TwoTruthsLiePhase.GAME_OVER
                    ) {
                        it
                    } else {
                        it.copy(saveError = error.message ?: "Your result could not be saved.")
                    }
                }
            }
        }
    }

    override fun onCleared() {
        audioJob?.cancel()
        roundTransitionJob?.cancel()
        playbackManager.release()
        super.onCleared()
    }

    private companion object {
        const val ROUND_COUNT = 5
        const val MAX_XP = 15
        const val FEEDBACK_DURATION_MS = 2_000L
    }
}
