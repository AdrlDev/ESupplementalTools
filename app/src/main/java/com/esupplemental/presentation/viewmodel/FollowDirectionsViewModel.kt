package com.esupplemental.presentation.viewmodel

import android.app.Application
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.usecases.games.SaveGameResultUseCase
import com.esupplemental.domain.utils.FollowDirectionsBank
import com.esupplemental.presentation.state.game_state.*
import com.esupplemental.domain.manager.MediaPlaybackManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FollowDirectionsViewModel(
    application: Application,
    private val repository: MediaRepository,
    private val playbackManager: MediaPlaybackManager,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    private val gameId: String,
    private val initialMediaId: String? = null
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(FollowDirectionsUiState())
    val uiState = _uiState.asStateFlow()

    private val roundCount = 4
    private var audioJob: Job? = null

    init {
        playbackManager.isPlaybackFinished
            .onEach { finished ->
                if (finished && _uiState.value.phase == FollowDirectionsPhase.LISTENING) {
                    _uiState.update { it.copy(phase = FollowDirectionsPhase.PLAYING) }
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
        FollowDirectionsBank.init(getApplication(), allStories)

        _uiState.update { 
            it.copy(
                storyContext = context,
                isLoading = false,
                totalRounds = roundCount,
                phase = FollowDirectionsPhase.IDLE
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

        val challenges = FollowDirectionsBank.getChallenges(context.mediaId)
        val challenge = challenges.getOrNull(nextRound - 1) ?: challenges.randomOrNull() ?: return
        val totalSteps = challenge.steps.sumOf { it.steps }
        val allowedMoves = totalSteps + 1

        _uiState.update {
            it.copy(
                phase = FollowDirectionsPhase.LOADING,
                currentChallenge = challenge,
                characterPosition = challenge.start,
                targetPosition = challenge.target,
                movesLeft = allowedMoves,
                maxMoves = allowedMoves,
                round = nextRound,
                score = if (resetGame) 0 else it.score,
                errorMessage = null
            )
        }

        playInstructionAudio(challenge)
    }

    private fun playInstructionAudio(challenge: DirectionChallenge) {
        audioJob?.cancel()
        audioJob = viewModelScope.launch {
            _uiState.update { it.copy(phase = FollowDirectionsPhase.LISTENING) }
            
            repository.generateAudioForText(
                id = challenge.id,
                title = "Follow the Directions",
                text = challenge.instructionText
            ).onSuccess { audioStory ->
                val mediaItem = MediaItem(
                    id = challenge.id,
                    title = "Instruction",
                    type = MediaType.STORY,
                    durationSeconds = audioStory.durationSeconds,
                    audioUrl = audioStory.url
                )
                playbackManager.prepare(mediaItem, emptyList(), emptyList())
                playbackManager.togglePlayPause()
            }.onFailure {
                // Fallback if audio fails
                delay(3000.milliseconds)
                _uiState.update { it.copy(phase = FollowDirectionsPhase.PLAYING) }
            }
        }
    }

    fun replayInstruction() {
        val challenge = _uiState.value.currentChallenge ?: return
        playInstructionAudio(challenge)
    }

    fun moveCharacter(direction: Direction) {
        val state = _uiState.value
        if (state.phase != FollowDirectionsPhase.PLAYING) return
        if (state.movesLeft <= 0) return

        val newPos = when(direction) {
            Direction.LEFT -> state.characterPosition.copy(x = (state.characterPosition.x - 1).coerceAtLeast(0))
            Direction.RIGHT -> state.characterPosition.copy(x = (state.characterPosition.x + 1).coerceAtMost(state.gridSize - 1))
            Direction.UP -> state.characterPosition.copy(y = (state.characterPosition.y - 1).coerceAtLeast(0))
            Direction.DOWN -> state.characterPosition.copy(y = (state.characterPosition.y + 1).coerceAtMost(state.gridSize - 1))
        }

        val remainingMoves = state.movesLeft - 1

        _uiState.update { 
            it.copy(
                characterPosition = newPos,
                movesLeft = remainingMoves
            ) 
        }

        if (newPos == state.targetPosition) {
            submitAnswer(true)
        } else if (remainingMoves <= 0) {
            submitAnswer(false)
        }
    }

    private fun submitAnswer(isCorrect: Boolean) {
        val state = _uiState.value
        if (state.phase != FollowDirectionsPhase.PLAYING) return

        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.update { 
            it.copy(
                phase = if (isCorrect) FollowDirectionsPhase.CORRECT else FollowDirectionsPhase.WRONG,
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

        _uiState.update { it.copy(phase = FollowDirectionsPhase.GAME_OVER, xpEarned = xp) }

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
        audioJob?.cancel()
        playbackManager.release()
    }
}
