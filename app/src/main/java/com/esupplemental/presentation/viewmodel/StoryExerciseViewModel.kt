package com.esupplemental.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.local.repository.QuizRepository
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.QuizResult
import com.esupplemental.data.model.MediaItem
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.presentation.state.StoryExerciseUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StoryExerciseViewModel(
    private val repository: MediaRepository,
    private val quizRepository: QuizRepository,
    private val playbackManager: MediaPlaybackManager,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(StoryExerciseUiState())
    val uiState: StateFlow<StoryExerciseUiState> = _uiState.asStateFlow()

    init {
        // Observe playback manager errors and propagate to UI state
        playbackManager.error
            .onEach { error ->
                _uiState.update { it.copy(audioError = error) }
            }
            .launchIn(viewModelScope)
    }

    fun dismissError() {
        _uiState.update { it.copy(audioError = null) }
    }

    fun dismissSubmissionError() {
        _uiState.update { it.copy(submissionError = null) }
    }

    fun playItemAudio(id: String, text: String) = viewModelScope.launch {
        _uiState.update { it.copy(loadingAudioIds = it.loadingAudioIds + id, audioError = null) }
        
        val result = repository.generateAudioForText(id, null, text)
        
        _uiState.update { it.copy(loadingAudioIds = it.loadingAudioIds - id) }
        
        result.onSuccess { audioStory ->
            // Create a dummy MediaItem for the playback manager
            val playbackMedia = MediaItem(
                id = id,
                title = text,
                type = _uiState.value.mediaType,
                durationSeconds = 0,
                audioUrl = audioStory.url
            )
            playbackManager.prepare(playbackMedia, emptyList())
            playbackManager.togglePlayPause()
        }.onFailure { error ->
            _uiState.update { it.copy(audioError = error.message) }
        }
    }

    override fun onCleared() {
        playbackManager.release()
    }

    fun loadActivity(mediaId: String, forceReset: Boolean = false) = viewModelScope.launch {
        val currentState = _uiState.value
        if (!forceReset && currentState.mediaId == mediaId && currentState.activity != null) {
            return@launch
        }

        val mediaType = repository.getMediaDetail(mediaId)?.type ?: MediaType.STORY
        val activity = repository.getStoryActivity(mediaId)
        _uiState.update {
            it.copy(
                mediaId = mediaId,
                mediaType = mediaType,
                activity = activity,
                orderedEvents = activity?.reorderEvents?.shuffled() ?: emptyList(),
                totalEvents = activity?.reorderEvents?.size ?: 0,
                mcAnswers = emptyMap(),
                mcScore = 0,
                isSubmitted = false,
                isSubmitting = false,
                reorderScore = 0,
                combinedScore = 0,
                combinedTotal = 0,
                userAnswersList = emptyList(),
                correctAnswersList = emptyList(),
                resultId = null,
                submissionError = null
            )
        }
    }

    /** Moves the event at [fromIndex] to [toIndex]. */
    fun moveEvent(fromIndex: Int, toIndex: Int) {
        if (_uiState.value.isSubmitted || _uiState.value.isSubmitting) return
        val list = _uiState.value.orderedEvents.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        _uiState.update { it.copy(orderedEvents = list) }
    }

    fun onMultipleChoiceChange(questionId: String, choiceIndex: Int) {
        if (_uiState.value.isSubmitted || _uiState.value.isSubmitting) return
        _uiState.update {
            it.copy(mcAnswers = it.mcAnswers + (questionId to choiceIndex))
        }
    }

    fun submit() {
        val initialState = _uiState.value
        if (initialState.isSubmitted || initialState.isSubmitting || initialState.activity == null) return

        _uiState.update { it.copy(isSubmitting = true, submissionError = null) }
        viewModelScope.launch {
            val state = _uiState.value
            val activity = state.activity ?: run {
                _uiState.update {
                    it.copy(isSubmitting = false, submissionError = "Exercise content is unavailable.")
                }
                return@launch
            }

            val userAnswers = mutableListOf<String>()
            val correctAnswers = mutableListOf<String>()
            var score = 0

            // 1. Reorder scoring
            val correctSequence = activity.reorderEvents.sortedBy { it.correctOrder }

            state.orderedEvents.forEachIndexed { index, event ->
                val correctOrderForThisEvent = activity.reorderEvents.find { it.id == event.id }?.correctOrder
                if (correctOrderForThisEvent == index + 1) {
                    score++
                }
                userAnswers.add("${index + 1}. ${event.description}")
            }

            correctSequence.forEach { event ->
                correctAnswers.add("${event.correctOrder}. ${event.description}")
            }

            // 2. Multiple choice scoring
            val mcQuestions = activity.multipleChoiceQuestions
            var mcScore = 0
            mcQuestions.forEach { q ->
                val selected = state.mcAnswers[q.id]
                val isCorrect = selected == q.correctAnswerIndex
                if (isCorrect) mcScore++

                userAnswers.add("MC: ${q.question} -> ${selected?.let { q.choices.getOrNull(it) } ?: "No answer"}")
                correctAnswers.add("MC: ${q.question} -> ${q.choices[q.correctAnswerIndex]}")
            }

            // 3. Combine both sections into one saved result.
            val combinedScore = score + mcScore
            val combinedTotal = activity.reorderEvents.size + mcQuestions.size

            val result = QuizResult(combinedScore, combinedTotal, userAnswers, correctAnswers, state.mediaType)
            val saveResponse = quizRepository.saveQuizResult(result)

            saveResponse.onSuccess { resultId ->
                _uiState.update {
                    it.copy(
                        isSubmitted = true,
                        isSubmitting = false,
                        reorderScore = score,
                        totalEvents = activity.reorderEvents.size,
                        mcScore = mcScore,
                        combinedScore = combinedScore,
                        combinedTotal = combinedTotal,
                        userAnswersList = userAnswers,
                        correctAnswersList = correctAnswers,
                        resultId = resultId,
                        submissionError = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isSubmitted = false,
                        resultId = null,
                        submissionError = error.message ?: "Unable to save your exercise. Please try again."
                    )
                }
            }
        }
    }
}
