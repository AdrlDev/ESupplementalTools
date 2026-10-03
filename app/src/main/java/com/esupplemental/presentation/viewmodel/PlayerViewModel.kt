package com.esupplemental.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.domain.usecases.media.GetMediaDetailUseCase
import com.esupplemental.domain.usecases.media.GetMediaItemsUseCase
import com.esupplemental.domain.usecases.media.ProcessTranscriptUseCase
import com.esupplemental.domain.model.media.StoryPage
import com.esupplemental.domain.model.media.StoryChunk
import com.esupplemental.domain.utils.StoryLoader
import com.esupplemental.domain.utils.PoemActivityFactory
import com.esupplemental.presentation.state.PlayerUiState
import com.esupplemental.data.remote.model.StoryAudioStatus
import com.esupplemental.data.remote.model.WordTiming
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Senior Engineer Note: This ViewModel is now clean and focuses purely on UI state coordination.
 * All complex business logic is delegated to Use Cases, and Android-specific playback
 * logic is abstracted into the MediaPlaybackManager.
 */
class PlayerViewModel(
    private val getMediaItemsUseCase: GetMediaItemsUseCase,
    private val getMediaDetailUseCase: GetMediaDetailUseCase,
    private val processTranscriptUseCase: ProcessTranscriptUseCase,
    private val playbackManager: MediaPlaybackManager,
    private val repository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private val loadingJobs = ConcurrentHashMap<String, Job>()

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        observePlaybackState()
    }

    /**
     * Observes playback manager flows and updates UI state reactively.
     */
    private fun observePlaybackState() {
        combine(
            playbackManager.isPlaying,
            playbackManager.isBuffering,
            playbackManager.error,
            playbackManager.currentPositionSeconds,
            playbackManager.isPlaybackFinished,
            playbackManager.amplitudes,
            playbackManager.currentSentenceIndex,
            playbackManager.mediaDurationSeconds,
            playbackManager.currentPositionMs,
            playbackManager.currentWordIndex
        ) { flows ->
            val playbackError = flows[2] as String?
            _uiState.update {
                it.copy(
                    isPlaying = flows[0] as Boolean,
                    isBuffering = flows[1] as Boolean,
                    error = playbackError ?: it.error,
                    currentPositionSeconds = flows[3] as Int,
                    isPlaybackFinished = flows[4] as Boolean,
                    amplitudes = flows[5] as List<Float>,
                    currentSentenceIndex = flows[6] as Int,
                    totalDurationSeconds = flows[7] as Int,
                    currentPositionMs = flows[8] as Long,
                    currentWordIndex = flows[9] as Int
                )
            }
        }.launchIn(viewModelScope)
    }

    // ── Load ──────────────────────────────────────────────────────────────────

    fun loadMedia(context: Context, mediaId: String) {
        val existingJob = loadingJobs[mediaId]

        if (existingJob?.isActive == true) {
            return
        }

        val job = viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoadingMedia = true,
                        error = null,
                        storyAudioStatus = null
                    )
                }

                val item =
                    getMediaDetailUseCase(mediaId)
                        ?: run {
                            _uiState.update {
                                it.copy(
                                    isLoadingMedia = false,
                                    error = "Media not found."
                                )
                            }

                            return@launch
                        }

                /*
                 * Load transcript.
                 */
                val loadedTranscript =
                    if (
                        (item.type == MediaType.STORY || item.type == MediaType.POEM) &&
                        item.transcript.endsWith(".md")
                    ) {
                        try {
                            StoryLoader.load(
                                context,
                                item.transcript
                            )
                        } catch (_: Exception) {
                            item.transcript
                        }
                    } else {
                        item.transcript
                    }

                val transcriptContent = if (item.type == MediaType.POEM) {
                    PoemActivityFactory.extractTranscript(loadedTranscript)
                } else {
                    loadedTranscript
                }

                /*
                 * Process transcript.
                 */
                val chunks =
                    if (item.type == MediaType.STORY || item.type == MediaType.POEM) {
                        processTranscriptUseCase(
                            transcriptContent
                        )
                    } else {
                        emptyList()
                    }

                /*
                 * Build readable story pages.
                 */
                val pages =
                    buildStoryPages(chunks)

                var finalItem = item
                var wordTimings =
                    emptyList<WordTiming>()

                /*
                 * =====================================================
                 * STORY AUDIO
                 * =====================================================
                 */
                if (item.type == MediaType.STORY || item.type == MediaType.POEM) {

                    _uiState.update {
                        it.copy(
                            storyAudioStatus =
                                StoryAudioStatus.PROCESSING
                        )
                    }

                    val result =
                        repository.generateAudioForText(
                            item.id,
                            item.title,
                            transcriptContent
                        )

                    result.fold(
                        onSuccess = { audioStory ->

                            if (audioStory.url.isBlank()) {
                                throw IllegalStateException(
                                    "Story audio URL is missing."
                                )
                            }

                            wordTimings =
                                audioStory.words

                            finalItem =
                                item.copy(
                                    audioUrl =
                                        audioStory.url
                                )

                            _uiState.update {
                                it.copy(
                                    storyAudioStatus =
                                        StoryAudioStatus.READY
                                )
                            }
                        },

                        onFailure = { throwable ->

                            _uiState.update {
                                it.copy(
                                    isLoadingMedia = false,
                                    storyAudioStatus =
                                        StoryAudioStatus.FAILED,
                                    error =
                                        throwable.message
                                            ?: "Unable to prepare story."
                                )
                            }

                            return@launch
                        }
                    )
                }

                /*
                 * Publish final playable state.
                 */
                _uiState.update {
                    it.copy(
                        mediaItem = finalItem,

                        transcriptChunks =
                            chunks,

                        transcriptPages =
                            pages,

                        wordTimings =
                            wordTimings,

                        currentSentenceIndex = 0,
                        currentWordIndex = -1,

                        isLoadingMedia = false,
                        error = null
                    )
                }

                playbackManager.prepare(
                    finalItem,
                    chunks,
                    wordTimings
                )

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoadingMedia = false,
                        storyAudioStatus =
                            if (
                                it.storyAudioStatus ==
                                StoryAudioStatus.PROCESSING
                            ) {
                                StoryAudioStatus.FAILED
                            } else {
                                it.storyAudioStatus
                            },
                        error =
                            e.message
                                ?: "Unable to load media."
                    )
                }

            } finally {
                loadingJobs.remove(mediaId)
            }
        }

        loadingJobs[mediaId] = job
    }

    private fun buildStoryPages(
        chunks: List<StoryChunk>,
        maxWordsPerPage: Int = 70
    ): List<StoryPage> {

        if (chunks.isEmpty()) {
            return emptyList()
        }

        val pages =
            mutableListOf<StoryPage>()

        var currentChunks =
            mutableListOf<StoryChunk>()

        var currentWordCount = 0
        var pageIndex = 0

        chunks.forEach { chunk ->

            val chunkWordCount =
                (
                        chunk.endWordIndex -
                                chunk.startWordIndex +
                                1
                        ).coerceAtLeast(1)

            if (
                currentChunks.isNotEmpty() &&
                currentWordCount + chunkWordCount >
                maxWordsPerPage
            ) {

                pages += StoryPage(
                    index = pageIndex++,
                    chunks =
                        currentChunks.toList()
                )

                currentChunks =
                    mutableListOf()

                currentWordCount = 0
            }

            currentChunks += chunk

            currentWordCount +=
                chunkWordCount
        }

        if (currentChunks.isNotEmpty()) {
            pages += StoryPage(
                index = pageIndex,
                chunks =
                    currentChunks.toList()
            )
        }

        return pages
    }

    // ── Playback controls ─────────────────────────────────────────────────────

    fun togglePlayPause() = playbackManager.togglePlayPause()

    fun skipForward() = playbackManager.skipForward()

    fun skipBackward() = playbackManager.skipBackward()

    fun seekTo(fraction: Float) = playbackManager.seekTo(fraction)

    fun seekToMs(positionMs: Long) = playbackManager.seekToMs(positionMs)


    // ── List / search ─────────────────────────────────────────────────────────

    fun loadListForType(type: MediaType) {
        _uiState.update { it.copy(currentListType = type, searchQuery = "") }
        fetchFilteredList(type, "")
    }

    fun onSearchQueryChange(query: String) {
        if (_uiState.value.searchQuery == query) return
        _uiState.update { it.copy(searchQuery = query) }
        fetchFilteredList(_uiState.value.currentListType, query)
    }

    private fun fetchFilteredList(type: MediaType, query: String) {
        searchJob?.cancel()
        searchJob = getMediaItemsUseCase.getFiltered(type, query)
            .onEach { filtered ->
                _uiState.update { it.copy(filteredList = filtered) }
            }
            .launchIn(viewModelScope)
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onCleared() {
        playbackManager.release()
    }
}
