package com.esupplemental.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.game.StoryQuestion
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.domain.usecases.games.SaveGameResultUseCase
import com.esupplemental.domain.utils.StoryOrderSummary
import com.esupplemental.presentation.state.game_state.StoryOrderUiState
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.CardCheckState
import com.esupplemental.presentation.ui.screens.game.easy.easy_enums.StoryOrderPhase
import kotlinx.coroutines.delay
import com.esupplemental.domain.model.media.StoryChunk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class StoryOrderViewModel(
    application: Application,
    private val repository: MediaRepository,
    private val playbackManager: MediaPlaybackManager,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    private val gameId: String,
    private val mediaId: String? = null
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(StoryOrderUiState())
    val uiState = _uiState.asStateFlow()

    private var loadedStories: List<StoryQuestion> = emptyList()

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        // Observe playback status to unlock UI when audio finishes
        playbackManager.isPlaying
            .onEach { isPlaying ->
                _uiState.update { it.copy(isAudioPlaying = isPlaying) }
            }
            .launchIn(viewModelScope)

        playbackManager.isBuffering
            .onEach { buffering ->
                _uiState.update { it.copy(isAudioBuffering = buffering) }
            }
            .launchIn(viewModelScope)

        playbackManager.currentSentenceIndex
            .onEach { index ->
                _uiState.update { state ->
                    state.copy(
                        currentCaptionIndex = index.coerceIn(
                            0,
                            (state.captionParagraphs.lastIndex).coerceAtLeast(0)
                        )
                    )
                }
            }
            .launchIn(viewModelScope)

        playbackManager.currentWordIndex
            .onEach { wordIndex ->
                _uiState.update { it.copy(currentWordIndex = wordIndex) }
            }
            .launchIn(viewModelScope)

        playbackManager.isPlaybackFinished
            .drop(1)
            .onEach { finished ->
                if (finished && _uiState.value.phase == StoryOrderPhase.LISTENING) {
                    _uiState.update {
                        it.copy(
                            hasListened = true,
                            isAudioPlaying = false,
                            phase = StoryOrderPhase.ORDERING
                        )
                    }
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
                            errorMessage = error
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

        loadGameData()
    }

    fun retryLoading() = loadGameData()

    private fun loadGameData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        runCatching {
            val gameStories = repository.getStoriesForGame(5)
            val sortedStories = if (!mediaId.isNullOrBlank()) {
                gameStories.sortedByDescending { it.id == mediaId }
            } else {
                gameStories
            }
            sortedStories.mapNotNull { story ->
                val activity = repository.getStoryActivity(story.id) ?: return@mapNotNull null
                val captionParagraphs = StoryOrderSummary.captionParagraphs(activity.reorderEvents)

                StoryQuestion(
                    id = story.id,
                    title = story.title,
                    thumbnailRes = story.thumbnailRes,
                    thumbnailUrl = story.thumbnailUrl,
                    storyText = captionParagraphs.joinToString("\n\n"),
                    captionParagraphs = captionParagraphs,
                    events = activity.reorderEvents
                )
            }
        }.onSuccess { questions ->
            loadedStories = questions
            if (questions.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No playable stories are available.") }
            } else {
                _uiState.update {
                    it.copy(totalPossibleEvents = questions.sumOf { question -> question.events.size })
                }
                loadQuestion(0)
                _uiState.update { it.copy(isLoading = false) }
            }
        }.onFailure { error ->
            _uiState.update {
                it.copy(isLoading = false, errorMessage = error.message ?: "Unable to load stories.")
            }
        }
    }

    // ── Load ──────────────────────────────────────────────────────────────────

    private fun loadQuestion(index: Int) {
        val story = loadedStories.getOrNull(index) ?: return
        val shuffled = story.events.shuffled()

        var currentWordOffset = 0
        val transcriptChunks = story.captionParagraphs.mapIndexed { chunkIndex, text ->
            val wordCount = text.split(Regex("\\s+")).filter { it.isNotEmpty() }.size
            val chunk = StoryChunk(
                index = chunkIndex,
                text = text,
                startWordIndex = currentWordOffset,
                endWordIndex = (currentWordOffset + wordCount - 1).coerceAtLeast(currentWordOffset)
            )
            currentWordOffset += wordCount
            chunk
        }

        _uiState.update {
            it.copy(
                errorMessage = null,
                phase = StoryOrderPhase.LISTENING,
                currentStory = story,
                userOrderedEvents = shuffled,
                cardCheckStates = emptyMap(),
                isAudioPlaying = false,
                isGeneratingAudio = false,
                isAudioBuffering = false,
                captionParagraphs = story.captionParagraphs,
                transcriptChunks = transcriptChunks,
                currentCaptionIndex = 0,
                currentWordIndex = -1,
                hasListened = false,
                questionIndex = index,
                totalQuestions = loadedStories.size
            )
        }
    }

    // ── Controls ──────────────────────────────────────────────────────────────

    fun playStory() = viewModelScope.launch {
        val story = _uiState.value.currentStory ?: return@launch
        if (_uiState.value.isAudioLoading) return@launch
        _uiState.update { it.copy(errorMessage = null) }
        
        if (_uiState.value.isAudioPlaying) {
            playbackManager.stop()
            _uiState.update { it.copy(isAudioPlaying = false) }
        } else {
            _uiState.update { it.copy(isGeneratingAudio = true) }
            val summaryAudioId = StoryOrderSummary.audioId(story.id, story.storyText)
            val result = repository.generateAudioForText(
                summaryAudioId,
                StoryOrderSummary.audioTitle(story.title),
                story.storyText
            )
            
            result.onSuccess { audioStory ->
                // Create a dummy MediaItem for playback
                val playbackMedia = MediaItem(
                    id = summaryAudioId,
                    title = story.title,
                    type = MediaType.STORY,
                    durationSeconds = audioStory.durationSeconds,
                    audioUrl = audioStory.url
                )
                
                val chunks = _uiState.value.transcriptChunks
                
                playbackManager.prepare(playbackMedia, chunks, audioStory.words)
                playbackManager.togglePlayPause()
                _uiState.update { it.copy(isGeneratingAudio = false) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isGeneratingAudio = false,
                        errorMessage = error.message ?: "Unable to play this story."
                    )
                }
            }
        }
    }

    fun startOrdering() {
        playbackManager.stop()
        _uiState.update { it.copy(phase = StoryOrderPhase.ORDERING, isAudioPlaying = false) }
    }

    /** Called after drag-to-reorder completes in the UI */
    fun reorderEvents(from: Int, to: Int) {
        val current = _uiState.value.userOrderedEvents.toMutableList()
        if (from !in current.indices || to !in current.indices) return
        val item = current.removeAt(from)
        current.add(to, item)
        _uiState.update { it.copy(userOrderedEvents = current) }
    }

    fun checkOrder() {
        val state = _uiState.value
        if (state.phase != StoryOrderPhase.ORDERING) return
        val story = state.currentStory ?: return
        val userOrder = state.userOrderedEvents

        val checkMap = mutableMapOf<String, CardCheckState>()
        var correct = 0

        userOrder.forEachIndexed { userPos, event ->
            val isCorrect = event.correctOrder == userPos + 1 // correctOrder is 1-based in activities
            checkMap[event.id] = if (isCorrect) CardCheckState.CORRECT else CardCheckState.WRONG
            if (isCorrect) correct++
        }

        val allCorrect = correct == story.events.size
        val xp = if (allCorrect) 15 else (correct * 3)

        _uiState.update {
            it.copy(
                phase = StoryOrderPhase.CHECKING,
                cardCheckStates = checkMap,
                correctCountThisRound = correct,
                totalCorrect = it.totalCorrect + correct,
                xpEarned = it.xpEarned + xp
            )
        }

        // After showing result briefly, move to next or game over
        viewModelScope.launch {
            delay(2200L.milliseconds)
            val nextIndex = state.questionIndex + 1
            if (nextIndex < loadedStories.size) {
                loadQuestion(nextIndex)
            } else {
                // ─── GAME OVER: SAVE RESULTS ───
                val finalState = _uiState.value

                // Calculate total possible points
                val totalPossibleEvents = finalState.totalPossibleEvents

                // Save to database
                saveGameResultUseCase(
                    gameId = gameId,
                    score = finalState.totalCorrect,
                    totalRounds = totalPossibleEvents,
                    xpEarned = finalState.xpEarned
                )

                // Update UI to Game Over
                _uiState.update { it.copy(phase = StoryOrderPhase.GAME_OVER) }
            }
        }
    }

    fun replayGame() {
        _uiState.update { it.copy(totalCorrect = 0, xpEarned = 0) }
        loadQuestion(0)
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onCleared() {
        super.onCleared()
        playbackManager.release()
    }
}
