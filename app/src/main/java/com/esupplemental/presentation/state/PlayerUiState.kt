package com.esupplemental.presentation.state

import androidx.compose.runtime.Immutable
import com.esupplemental.data.model.*
import com.esupplemental.domain.model.media.*

import com.esupplemental.data.remote.model.StoryAudioStatus
import com.esupplemental.data.remote.model.WordTiming

@Immutable
data class PlayerUiState(
    val mediaItem: MediaItem? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isLoadingMedia: Boolean = false,
    val isProcessing: Boolean = false,
    val storyAudioStatus: StoryAudioStatus? = null,
    val error: String? = null,
    val currentPositionSeconds: Int = 0,
    val currentPositionMs: Long = 0L,
    val totalDurationSeconds: Int = 0,
    val currentSentenceIndex: Int = -1,
    val currentWordIndex: Int = -1,
    val transcriptChunks: List<StoryChunk> = emptyList(),
    val transcriptPages: List<StoryPage> = emptyList(),
    val wordTimings: List<WordTiming> = emptyList(),
    val amplitudes: List<Float> = listOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f), // Normalized 0.0 to 1.0
    val isPlaybackFinished: Boolean = false,
    val showTranscript: Boolean = false,
    val searchQuery: String = "",
    val songs: List<MediaItem> = emptyList(),
    val stories: List<MediaItem> = emptyList(),
    val filteredList: List<MediaItem> = emptyList(),
    val currentListType: MediaType = MediaType.SONG
)
