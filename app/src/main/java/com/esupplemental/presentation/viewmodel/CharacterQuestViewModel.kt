package com.esupplemental.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.domain.usecases.games.SaveGameResultUseCase
import com.esupplemental.domain.utils.CharacterQuestBank
import com.esupplemental.presentation.state.game_state.CharacterQuestUiState
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.CharacterQuestPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class CharacterQuestViewModel(
    application: Application,
    private val repository: MediaRepository,
    private val playbackManager: MediaPlaybackManager,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    private val gameId: String,
    private val mediaId: String? = null
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CharacterQuestUiState())
    val uiState = _uiState.asStateFlow()

    private var transitionJob: Job? = null

    init {
        playbackManager.isPlaying
            .onEach { isPlaying ->
                _uiState.update { it.copy(isAudioPlaying = isPlaying) }
            }
            .launchIn(viewModelScope)

        playbackManager.isBuffering
            .onEach { isBuffering ->
                _uiState.update { it.copy(isAudioBuffering = isBuffering) }
            }
            .launchIn(viewModelScope)

        playbackManager.isPlaybackFinished
            .drop(1)
            .onEach { finished ->
                if (finished && _uiState.value.phase == CharacterQuestPhase.LISTENING) {
                    _uiState.update { it.copy(phase = CharacterQuestPhase.SELECTING, isAudioPlaying = false) }
                }
            }
            .launchIn(viewModelScope)

        playbackManager.error
            .drop(1)
            .onEach { error ->
                if (error != null) {
                    _uiState.update {
                        it.copy(
                            isGeneratingAudio = false,
                            isAudioBuffering = false,
                            isAudioPlaying = false,
                            phase = CharacterQuestPhase.SELECTING,
                            errorMessage = error
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

        loadGame()
    }

    fun loadGame() {
        val rounds = CharacterQuestBank.randomRounds(context = getApplication(), count = 5, mediaId = mediaId)
        if (rounds.isEmpty()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "No questions available.") }
            return
        }

        _uiState.update {
            CharacterQuestUiState(
                isLoading = false,
                errorMessage = null,
                phase = CharacterQuestPhase.LISTENING,
                questions = rounds,
                questionIndex = 0,
                totalQuestions = rounds.size,
                currentQuestion = rounds.firstOrNull(),
                selectedCharacterIndex = null,
                isCorrect = null,
                score = 0,
                xpEarned = 0
            )
        }

        playAudio()
    }

    fun playAudio() = viewModelScope.launch {
        val currentQ = _uiState.value.currentQuestion ?: return@launch
        if (_uiState.value.isAudioLoading) return@launch

        _uiState.update { it.copy(errorMessage = null) }

        if (_uiState.value.isAudioPlaying) {
            playbackManager.stop()
            _uiState.update { it.copy(isAudioPlaying = false) }
        } else {
            _uiState.update { it.copy(isGeneratingAudio = true) }
            val result = repository.generateAudioForText(
                id = currentQ.id,
                title = "${currentQ.storyTitle} — Who Said It?",
                text = currentQ.audioText
            )

            result.onSuccess { audioStory ->
                val mediaItem = MediaItem(
                    id = currentQ.id,
                    title = currentQ.storyTitle,
                    type = MediaType.STORY,
                    durationSeconds = audioStory.durationSeconds,
                    audioUrl = audioStory.url
                )
                playbackManager.prepare(mediaItem, emptyList())
                playbackManager.togglePlayPause()
                _uiState.update { it.copy(isGeneratingAudio = false) }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isGeneratingAudio = false,
                        phase = CharacterQuestPhase.SELECTING,
                        errorMessage = err.message ?: "Unable to play audio. Tap a character to answer!"
                    )
                }
            }
        }
    }

    fun selectCharacter(index: Int) {
        val state = _uiState.value
        if (state.phase == CharacterQuestPhase.FEEDBACK || state.phase == CharacterQuestPhase.GAME_OVER) return
        val currentQ = state.currentQuestion ?: return

        playbackManager.stop()

        val isRight = index == currentQ.correctIndex
        val addedScore = if (isRight) 1 else 0
        val addedXp = if (isRight) 15 else 3

        _uiState.update {
            it.copy(
                phase = CharacterQuestPhase.FEEDBACK,
                selectedCharacterIndex = index,
                isCorrect = isRight,
                score = it.score + addedScore,
                xpEarned = it.xpEarned + addedXp,
                isAudioPlaying = false
            )
        }

        transitionJob?.cancel()
        transitionJob = viewModelScope.launch {
            delay(2400.milliseconds)
            advanceNextRound()
        }
    }

    fun skipToSelection() {
        playbackManager.stop()
        _uiState.update { it.copy(phase = CharacterQuestPhase.SELECTING, isAudioPlaying = false) }
    }

    private fun advanceNextRound() {
        val state = _uiState.value
        val nextIdx = state.questionIndex + 1

        if (nextIdx < state.questions.size) {
            val nextQ = state.questions[nextIdx]
            _uiState.update {
                it.copy(
                    phase = CharacterQuestPhase.LISTENING,
                    questionIndex = nextIdx,
                    currentQuestion = nextQ,
                    selectedCharacterIndex = null,
                    isCorrect = null,
                    isAudioPlaying = false,
                    isGeneratingAudio = false,
                    isAudioBuffering = false,
                    errorMessage = null
                )
            }
            playAudio()
        } else {
            // Game Over
            val finalState = _uiState.value
            viewModelScope.launch {
                saveGameResultUseCase(
                    gameId = gameId,
                    score = finalState.score,
                    totalRounds = finalState.totalQuestions,
                    xpEarned = finalState.xpEarned
                )
            }
            _uiState.update { it.copy(phase = CharacterQuestPhase.GAME_OVER) }
        }
    }

    fun replayGame() {
        transitionJob?.cancel()
        loadGame()
    }

    override fun onCleared() {
        super.onCleared()
        transitionJob?.cancel()
        playbackManager.release()
    }
}
