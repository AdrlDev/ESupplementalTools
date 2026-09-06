package com.esupplemental.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.game.DisappearingTextQuestion
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.domain.usecases.games.SaveGameResultUseCase
import com.esupplemental.domain.model.media.StoryChunk
import com.esupplemental.domain.utils.DisappearingTextBank
import com.esupplemental.presentation.state.game_state.DisappearingTextUiState
import com.esupplemental.presentation.ui.screens.game.moderate.DisappearingTextPhase
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
import kotlin.time.Duration.Companion.milliseconds

class DisappearingTextViewModel(
    application: Application,
    private val repository: MediaRepository,
    private val playbackManager: MediaPlaybackManager,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    private val gameId: String,
    private val mediaId: String? = null
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DisappearingTextUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var audioJob: Job? = null
    private var availableQuestions: List<DisappearingTextQuestion> = emptyList()
    private var roundQuestions: List<DisappearingTextQuestion> = emptyList()
    private var narrationCompleted = false

    init {
        playbackManager.isPlaying
            .onEach { playing ->
                _uiState.update { state ->
                    if (state.phase != DisappearingTextPhase.MEMORIZING) state
                    else state.copy(
                        isAudioPlaying = playing,
                        isAudioLoading = if (playing) false else state.isAudioLoading,
                        highlightedWordIndex = if (playing) {
                            state.highlightedWordIndex.coerceAtLeast(0)
                        } else {
                            -1
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        playbackManager.isBuffering
            .onEach { buffering ->
                if (buffering && _uiState.value.phase == DisappearingTextPhase.MEMORIZING) {
                    _uiState.update { it.copy(isAudioLoading = true) }
                }
            }
            .launchIn(viewModelScope)

        playbackManager.currentSentenceIndex
            .onEach { wordIndex ->
                _uiState.update { state ->
                    if (state.phase == DisappearingTextPhase.MEMORIZING && state.isAudioPlaying) {
                        val lastWordIndex = (state.visibleWordCount - 1).coerceAtLeast(0)
                        state.copy(highlightedWordIndex = wordIndex.coerceIn(0, lastWordIndex))
                    } else {
                        state
                    }
                }
            }
            .launchIn(viewModelScope)

        playbackManager.isPlaybackFinished
            .onEach { finished ->
                if (finished) completeNarration()
            }
            .launchIn(viewModelScope)

        playbackManager.error
            .onEach { error ->
                if (error != null) continueWithoutNarration(error)
            }
            .launchIn(viewModelScope)

        loadQuestions()
    }

    private fun loadQuestions(startWhenReady: Boolean = false) = viewModelScope.launch {
        val questions = withContext(Dispatchers.IO) {
            DisappearingTextBank.load(getApplication())
        }
        availableQuestions = questions
        _uiState.update {
            it.copy(
                isContentLoading = false,
                errorMessage = if (questions.isEmpty()) {
                    "No suitable sentences were found in the story assets."
                } else {
                    null
                }
            )
        }
        if (startWhenReady && questions.isNotEmpty()) {
            startGame()
        }
    }

    fun startGame() {
        if (_uiState.value.isContentLoading) return
        if (availableQuestions.isEmpty()) {
            _uiState.update { it.copy(isContentLoading = true, errorMessage = null) }
            loadQuestions(startWhenReady = true)
            return
        }

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

        timerJob?.cancel()
        audioJob?.cancel()
        playbackManager.stop()
        narrationCompleted = false

        _uiState.update {
            it.copy(
                errorMessage = null,
                phase = DisappearingTextPhase.MEMORIZING,
                currentQuestion = question,
                visibleWordCount = question.words().size,
                highlightedWordIndex = -1,
                isAudioLoading = true,
                isAudioPlaying = false,
                userInput = "",
                round = nextRound,
                readTimerMs = it.maxReadTimerMs,
                score = if (resetGame) 0 else it.score,
                xpEarned = if (resetGame) 0 else it.xpEarned
            )
        }

        playCurrentSentence(question)
    }

    private fun playCurrentSentence(question: DisappearingTextQuestion) {
        audioJob = viewModelScope.launch {
            repository.generateAudioForText(
                id = question.id,
                title = "Disappearing Text",
                text = question.sentence
            ).onSuccess { audioStory ->
                if (_uiState.value.currentQuestion?.id != question.id) return@onSuccess

                val playbackMedia = MediaItem(
                    id = question.id,
                    title = question.sentence,
                    type = MediaType.STORY,
                    durationSeconds = audioStory.durationSeconds,
                    audioUrl = audioStory.url
                )
                
                val transcriptChunks = question.words().mapIndexed { index, word ->
                    StoryChunk(
                        index = index,
                        text = word,
                        startWordIndex = index,
                        endWordIndex = index
                    )
                }
                
                playbackManager.prepare(playbackMedia, transcriptChunks, audioStory.words)
                playbackManager.togglePlayPause()
            }.onFailure { error ->
                continueWithoutNarration(
                    error.message ?: "The sentence audio could not be played."
                )
            }
        }
    }

    private fun completeNarration() {
        if (narrationCompleted || _uiState.value.phase != DisappearingTextPhase.MEMORIZING) return
        narrationCompleted = true
        _uiState.update {
            it.copy(
                isAudioLoading = false,
                isAudioPlaying = false,
                highlightedWordIndex = -1
            )
        }
        startReadTimer()
    }

    private fun continueWithoutNarration(message: String) {
        if (narrationCompleted || _uiState.value.phase != DisappearingTextPhase.MEMORIZING) return
        _uiState.update { it.copy(errorMessage = message) }
        completeNarration()
    }

    private fun startReadTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val tick = 100L
            while (_uiState.value.readTimerMs > 0) {
                delay(tick.milliseconds)
                _uiState.update { it.copy(readTimerMs = (it.readTimerMs - tick).coerceAtLeast(0)) }
            }
            startDisappearingAnimation()
        }
    }

    private fun startDisappearingAnimation() {
        playbackManager.stop()
        _uiState.update {
            it.copy(
                phase = DisappearingTextPhase.DISAPPEARING,
                isAudioLoading = false,
                isAudioPlaying = false,
                highlightedWordIndex = -1
            )
        }
        viewModelScope.launch {
            val words = _uiState.value.currentQuestion?.words().orEmpty()
            for (i in words.size downTo 0) {
                _uiState.update { it.copy(visibleWordCount = i) }
                delay(400L.milliseconds)
            }
            _uiState.update { it.copy(phase = DisappearingTextPhase.RECALLING) }
        }
    }

    fun onUserInputChange(input: String) {
        _uiState.update { it.copy(userInput = input) }
    }

    fun submitAnswer() {
        val state = _uiState.value
        if (state.phase != DisappearingTextPhase.RECALLING) return
        val original = state.currentQuestion?.sentence?.trim()?.lowercase() ?: ""
        val userTyped = state.userInput.trim().lowercase()

        // Very basic validation: ignore punctuation for memory check
        val cleanOriginal = original.replace(Regex("[^a-z0-9\\s]"), "")
        val cleanUser = userTyped.replace(Regex("[^a-z0-9\\s]"), "")

        val isCorrect = cleanOriginal == cleanUser
        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.update {
            it.copy(
                phase = if (isCorrect) DisappearingTextPhase.CORRECT else DisappearingTextPhase.WRONG,
                score = newScore
            )
        }

        viewModelScope.launch {
            delay(2000L.milliseconds)
            if (state.round >= state.totalRounds) {
                finishGame()
            } else {
                loadNextRound()
            }
        }
    }

    private fun finishGame() {
        val state = _uiState.value
        val xp = (state.score.toFloat() / state.totalRounds * 15).toInt() // Moderate gives more XP

        _uiState.update { it.copy(phase = DisappearingTextPhase.GAME_OVER, xpEarned = xp) }

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
        timerJob?.cancel()
        audioJob?.cancel()
        playbackManager.release()
    }

    private fun DisappearingTextQuestion.words(): List<String> =
        sentence.split(WHITESPACE).filter { it.isNotBlank() }

    private companion object {
        const val ROUND_COUNT = 5
        val WHITESPACE = Regex("\\s+")
    }
}
