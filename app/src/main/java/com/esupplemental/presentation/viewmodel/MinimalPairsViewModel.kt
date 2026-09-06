package com.esupplemental.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.game.MinimalPairQuestion
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.domain.usecases.games.SaveGameResultUseCase
import com.esupplemental.domain.utils.MinimalPairsBank
import com.esupplemental.presentation.state.game_state.MinimalPairsUiState
import com.esupplemental.presentation.ui.screens.game.moderate.MinimalPairsPhase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MinimalPairsViewModel(
    application: Application,
    private val repository: MediaRepository,
    private val playbackManager: MediaPlaybackManager,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    private val gameId: String,
    private val mediaId: String? = null
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MinimalPairsUiState())
    val uiState = _uiState.asStateFlow()

    private var availableQuestions: List<MinimalPairQuestion> = emptyList()
    private var roundQuestions: List<MinimalPairQuestion> = emptyList()
    private var audioJob: Job? = null

    init {
        playbackManager.isPlaying
            .onEach { playing ->
                _uiState.update { state ->
                    state.copy(
                        isAudioPlaying = playing,
                        isAudioLoading = if (playing) false else state.isAudioLoading
                    )
                }
            }
            .launchIn(viewModelScope)

        playbackManager.isBuffering
            .onEach { buffering ->
                if (buffering && _uiState.value.phase == MinimalPairsPhase.LISTENING) {
                    _uiState.update { it.copy(isAudioLoading = true) }
                }
            }
            .launchIn(viewModelScope)

        playbackManager.isPlaybackFinished
            .onEach { finished ->
                if (finished) unlockSelection()
            }
            .launchIn(viewModelScope)

        playbackManager.error
            .onEach { error ->
                if (error != null) unlockSelection(error)
            }
            .launchIn(viewModelScope)

        loadQuestions()
    }

    private fun loadQuestions(startWhenReady: Boolean = false) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) {
            runCatching { MinimalPairsBank.load(getApplication<Application>()) }
        }

        result.onSuccess { questions ->
            availableQuestions = questions
            _uiState.update {
                it.copy(
                    isContentLoading = false,
                    errorMessage = if (questions.size < ROUND_COUNT) {
                        "Not enough minimal pairs were found in the story assets."
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
                    errorMessage = error.message ?: "Unable to load story vocabulary."
                )
            }
        }
    }

    fun startGame() {
        if (_uiState.value.isContentLoading) return
        if (availableQuestions.size < ROUND_COUNT) {
            _uiState.update { it.copy(isContentLoading = true, errorMessage = null) }
            loadQuestions(startWhenReady = true)
            return
        }

        roundQuestions = selectRoundQuestions()
        _uiState.update { it.copy(totalRounds = roundQuestions.size) }
        loadNextRound(resetGame = true)
    }

    private fun selectRoundQuestions(): List<MinimalPairQuestion> {
        val shuffled = availableQuestions.shuffled()
        val usedWords = mutableSetOf<String>()
        val nonRepeating = shuffled.filter { question ->
            val isNewPair = question.wordA !in usedWords && question.wordB !in usedWords
            if (isNewPair) {
                usedWords += question.wordA
                usedWords += question.wordB
            }
            isNewPair
        }
        val selectedIds = nonRepeating.mapTo(mutableSetOf()) { it.id }
        return (nonRepeating + shuffled.filterNot { it.id in selectedIds }).take(ROUND_COUNT)
    }

    private fun loadNextRound(resetGame: Boolean = false) {
        val nextRound = if (resetGame) 1 else _uiState.value.round + 1
        val question = roundQuestions.getOrNull(nextRound - 1) ?: return
        val spokenWord = listOf(question.wordA, question.wordB).random()

        audioJob?.cancel()
        playbackManager.stop()
        _uiState.update {
            it.copy(
                phase = MinimalPairsPhase.LISTENING,
                currentQuestion = question.copy(correctWord = spokenWord),
                selectedWord = null,
                isCorrect = null,
                round = nextRound,
                score = if (resetGame) 0 else it.score,
                xpEarned = if (resetGame) 0 else it.xpEarned,
                isAudioLoading = false,
                isAudioPlaying = false,
                errorMessage = null
            )
        }

        playAudio()
    }

    fun playAudio() {
        val state = _uiState.value
        if (
            state.phase != MinimalPairsPhase.LISTENING &&
            state.phase != MinimalPairsPhase.SELECTING
        ) return
        if (state.isAudioLoading || state.isAudioPlaying) return
        val word = state.currentQuestion?.correctWord ?: return

        audioJob?.cancel()
        playbackManager.stop()
        _uiState.update {
            it.copy(
                phase = MinimalPairsPhase.LISTENING,
                isAudioLoading = true,
                isAudioPlaying = false,
                errorMessage = null
            )
        }

        audioJob = viewModelScope.launch {
            val audioId = MinimalPairsBank.audioId(word)
            repository.generateAudioForText(
                id = audioId,
                title = word,
                text = word
            ).onSuccess { audioStory ->
                if (_uiState.value.currentQuestion?.correctWord != word) return@onSuccess

                val playbackMedia = MediaItem(
                    id = audioId,
                    title = word,
                    type = MediaType.SONG,
                    durationSeconds = audioStory.durationSeconds,
                    audioUrl = audioStory.url
                )
                playbackManager.prepare(playbackMedia, emptyList())
                playbackManager.togglePlayPause()
            }.onFailure { error ->
                unlockSelection(error.message ?: "Unable to play the word audio.")
            }
        }
    }

    private fun unlockSelection(errorMessage: String? = null) {
        if (_uiState.value.phase != MinimalPairsPhase.LISTENING) return
        _uiState.update {
            it.copy(
                phase = MinimalPairsPhase.SELECTING,
                isAudioLoading = false,
                isAudioPlaying = false,
                errorMessage = errorMessage
            )
        }
    }

    fun onWordSelected(word: String) {
        if (_uiState.value.phase != MinimalPairsPhase.SELECTING) return

        val state = _uiState.value
        val isCorrect = word == state.currentQuestion?.correctWord
        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.update {
            it.copy(
                phase = if (isCorrect) MinimalPairsPhase.CORRECT else MinimalPairsPhase.WRONG,
                selectedWord = word,
                isCorrect = isCorrect,
                score = newScore
            )
        }

        viewModelScope.launch {
            delay(1500L)
            if (state.round >= state.totalRounds) {
                finishGame()
            } else {
                loadNextRound()
            }
        }
    }

    private fun finishGame() {
        val state = _uiState.value
        val xp = (state.score.toFloat() / state.totalRounds * 15).toInt()

        _uiState.update { it.copy(phase = MinimalPairsPhase.GAME_OVER, xpEarned = xp) }

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
        audioJob?.cancel()
        playbackManager.release()
        super.onCleared()
    }

    private companion object {
        const val ROUND_COUNT = 8
    }
}
