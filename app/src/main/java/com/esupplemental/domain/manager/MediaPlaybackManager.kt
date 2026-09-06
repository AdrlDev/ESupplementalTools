package com.esupplemental.domain.manager

import com.esupplemental.data.model.MediaItem
import kotlinx.coroutines.flow.StateFlow

interface MediaPlaybackManager {
    val isPlaying: StateFlow<Boolean>
    val isBuffering: StateFlow<Boolean>
    val error: StateFlow<String?>
    val currentPositionSeconds: StateFlow<Int>
    val currentPositionMs: StateFlow<Long>
    val isPlaybackFinished: StateFlow<Boolean>
    val amplitudes: StateFlow<List<Float>>
    val currentSentenceIndex: StateFlow<Int>
    val currentWordIndex: StateFlow<Int>
    val mediaDurationSeconds: StateFlow<Int>

    fun prepare(
        item: MediaItem, 
        transcriptChunks: List<com.esupplemental.domain.model.media.StoryChunk>,
        wordTimings: List<com.esupplemental.data.remote.model.WordTiming> = emptyList()
    )
    fun togglePlayPause()
    fun seekTo(fraction: Float)
    fun seekToMs(positionMs: Long)
    fun skipForward()
    fun skipBackward()
    fun stop()
    fun release()
}
