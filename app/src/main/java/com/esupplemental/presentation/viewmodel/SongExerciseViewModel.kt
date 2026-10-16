package com.esupplemental.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.local.repository.QuizRepository
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.QuizResult
import com.esupplemental.data.model.MediaItem
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.presentation.state.SongExerciseUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SongExerciseViewModel(
    private val repository: MediaRepository,
    private val quizRepository: QuizRepository,
    private val playbackManager: MediaPlaybackManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SongExerciseUiState())
    val uiState: StateFlow<SongExerciseUiState> = _uiState.asStateFlow()

    init {
        combine(
            playbackManager.isPlaying,
            playbackManager.error
        ) { isPlaying, error -> isPlaying to error }
            .onEach { (isPlaying, error) ->
                _uiState.update { state ->
                    state.copy(
                        playingAudioId = if (isPlaying) state.playingAudioId else null,
                        audioError = error
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun playPodcastQuestionAudio(id: String, text: String) {
        val state = _uiState.value
        if (!state.isPodcast || state.isSubmitted || state.isSubmitting) return

        if (state.playingAudioId == id) {
            playbackManager.togglePlayPause()
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadingAudioIds = it.loadingAudioIds + id,
                    audioError = null,
                    playingAudioId = null
                )
            }

            repository.generateAudioForText(
                id = id,
                title = "Podcast question",
                text = text.replace("___", "blank")
            ).onSuccess { audioStory ->
                playbackManager.prepare(
                    MediaItem(
                        id = id,
                        title = "Podcast question",
                        type = MediaType.PODCAST,
                        durationSeconds = audioStory.durationSeconds,
                        audioUrl = audioStory.url
                    ),
                    emptyList()
                )
                playbackManager.togglePlayPause()
                _uiState.update { it.copy(playingAudioId = id) }
            }.onFailure { error ->
                _uiState.update { it.copy(audioError = error.message ?: "Unable to play this question.") }
            }

            _uiState.update { it.copy(loadingAudioIds = it.loadingAudioIds - id) }
        }
    }

    fun dismissAudioError() = _uiState.update { it.copy(audioError = null) }

    override fun onCleared() {
        playbackManager.release()
    }

    fun loadActivity(mediaId: String, forceReset: Boolean = false) = viewModelScope.launch {
        val current = _uiState.value
        if (!forceReset && current.mediaId == mediaId && current.activity != null) return@launch

        _uiState.update { it.copy(mediaId = mediaId, loading = true, error = null) }
        runCatching {
            repository.getSongActivity(mediaId) to repository.getMediaDetail(mediaId)
        }
            .onSuccess { (activity, media) ->
                _uiState.update {
                    it.copy(
                        mediaId = mediaId,
                        isPodcast = media?.type == MediaType.PODCAST,
                        activity = activity,
                        fillBlankAnswers = emptyMap(),
                        selectedMessageOption = -1,
                        isSubmitted = false,
                        isSubmitting = false,
                        score = 0,
                        total = (activity?.fillBlanks?.size ?: 0) + if (activity != null) 1 else 0,
                        userAnswersList = emptyList(),
                        correctAnswersList = emptyList(),
                        resultId = null,
                        loading = false,
                        error = if (activity == null) "This challenge is unavailable." else null,
                        submissionError = null
                    )
                }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(loading = false, activity = null, error = error.message ?: "Unable to load this challenge.")
                }
            }
    }

    fun onFillBlankAnswerChange(itemId: String, answer: String) {
        if (_uiState.value.isSubmitted || _uiState.value.isSubmitting) return
        _uiState.update { it.copy(fillBlankAnswers = it.fillBlankAnswers + (itemId to answer)) }
    }

    fun onMessageOptionSelected(index: Int) {
        if (_uiState.value.isSubmitted || _uiState.value.isSubmitting) return
        _uiState.update { it.copy(selectedMessageOption = index) }
    }

    fun dismissSubmissionError() = _uiState.update { it.copy(submissionError = null) }

    fun submit() {
        val initial = _uiState.value
        if (initial.activity == null || initial.isSubmitted || initial.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true, submissionError = null) }

        viewModelScope.launch {
            val state = _uiState.value
            val activity = state.activity ?: return@launch
            val userAnswers = mutableListOf<String>()
            val correctAnswers = mutableListOf<String>()
            var score = 0

            activity.fillBlanks.forEachIndexed { index, item ->
                val answer = state.fillBlankAnswers[item.id].orEmpty()
                val correct = normalize(answer) == normalize(item.answer)
                if (correct) score++
                userAnswers += "${index + 1}. ${answer.ifBlank { "No answer" }}"
                correctAnswers += "${index + 1}. ${item.answer}"
            }

            val message = activity.messageQuestion
            val selected = state.selectedMessageOption
            if (selected == message.correctIndex) score++
            userAnswers += "Message: ${message.options.getOrNull(selected) ?: "No answer"}"
            correctAnswers += "Message: ${message.options[message.correctIndex]}"

            val resultType = if (state.isPodcast) MediaType.PODCAST else MediaType.SONG
            val result = QuizResult(score, state.total, userAnswers, correctAnswers, resultType)
            quizRepository.saveQuizResult(result)
                .onSuccess { resultId ->
                    _uiState.update {
                        it.copy(isSubmitting = false, isSubmitted = true, score = score, resultId = resultId)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isSubmitting = false, submissionError = error.message ?: "Unable to save your challenge. Please try again.")
                    }
                }
        }
    }

    private fun normalize(value: String): String = value
        .trim()
        .lowercase()
        .replace(Regex("[^\\p{L}\\p{N}']+"), " ")
        .trim()
}
