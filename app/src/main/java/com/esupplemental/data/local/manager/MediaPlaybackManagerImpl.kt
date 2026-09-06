package com.esupplemental.data.local.manager

import android.app.Application
import android.media.audiofx.Visualizer
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.MediaItem as Media3Item
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.domain.utils.DriveAudioManager
import com.esupplemental.domain.utils.ErrorMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Senior Engineer Note: TTS has been removed. All playback now uses ExoPlayer.
 * Transcript synchronization for stories is handled via time-based interpolation
 * in the ticker job.
 */
class MediaPlaybackManagerImpl(
    private val application: Application
) : MediaPlaybackManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var tickerJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    override val isBuffering = _isBuffering.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error = _error.asStateFlow()

    private val _currentPositionSeconds = MutableStateFlow(0)
    override val currentPositionSeconds = _currentPositionSeconds.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    override val currentPositionMs = _currentPositionMs.asStateFlow()

    private val _isPlaybackFinished = MutableStateFlow(false)
    override val isPlaybackFinished = _isPlaybackFinished.asStateFlow()

    private val _amplitudes = MutableStateFlow(listOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f))
    override val amplitudes = _amplitudes.asStateFlow()

    private val _currentSentenceIndex = MutableStateFlow(0)
    override val currentSentenceIndex = _currentSentenceIndex.asStateFlow()

    private val _currentWordIndex = MutableStateFlow(-1)
    override val currentWordIndex = _currentWordIndex.asStateFlow()

    private val _mediaDurationSeconds = MutableStateFlow(0)
    override val mediaDurationSeconds = _mediaDurationSeconds.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var visualizer: Visualizer? = null

    private var mediaItem: MediaItem? = null
    private var storyChunks: List<com.esupplemental.domain.model.media.StoryChunk> = emptyList()
    private var wordTimings: List<com.esupplemental.data.remote.model.WordTiming> = emptyList()

    init {
        initExoPlayer()
    }

    private fun initExoPlayer() {
        exoPlayer = ExoPlayer.Builder(application).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    _isPlaying.value = playing
                    if (playing) {
                        _isPlaybackFinished.value = false
                        val currentType = mediaItem?.type ?: MediaType.SONG
                        startTickerJob(currentType)
                    } else {
                        visualizer?.enabled = false
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    _isBuffering.value = state == Player.STATE_BUFFERING
                    if (state == Player.STATE_READY) {
                        val duration = (exoPlayer?.duration ?: 0L) / 1000
                        if (duration > 0) {
                            _mediaDurationSeconds.value = duration.toInt()
                        }
                        setupVisualizer()
                        if (_isPlaying.value) visualizer?.enabled = true
                    }
                    if (state == Player.STATE_ENDED) {
                        _isPlaying.value = false
                        _currentPositionSeconds.value = 0
                        _currentPositionMs.value = 0L
                        _currentSentenceIndex.value = 0
                        _currentWordIndex.value = -1
                        _isPlaybackFinished.value = true
                        _amplitudes.value = listOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f)
                        tickerJob?.cancel()
                        visualizer?.enabled = false
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    _isPlaying.value = false
                    val humanReadableMessage = ErrorMapper.map(error)
                    _error.value = humanReadableMessage
                    Log.e("MediaPlaybackManager", "ExoPlayer Error ($humanReadableMessage): ${error.message}", error)
                }
            })
        }
    }

    override fun prepare(
        item: MediaItem, 
        transcriptChunks: List<com.esupplemental.domain.model.media.StoryChunk>,
        wordTimings: List<com.esupplemental.data.remote.model.WordTiming>
    ) {
        mediaItem = item
        _isPlaybackFinished.value = false
        _isPlaying.value = false
        _error.value = null
        _currentPositionSeconds.value = 0
        _currentPositionMs.value = 0L
        _currentSentenceIndex.value = 0
        _currentWordIndex.value = -1
        _mediaDurationSeconds.value = item.durationSeconds
        this.storyChunks = transcriptChunks
        this.wordTimings = wordTimings

        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()


        val media3Item = when {
            !item.audioUrl.isNullOrBlank() -> {
                val resolvedUrl = DriveAudioManager.getDirectStreamUrl(item.audioUrl)
                Media3Item.fromUri(resolvedUrl ?: item.audioUrl)
            }
            item.audioRes != null && item.audioRes != 0 -> {
                val uri = "android.resource://${application.packageName}/${item.audioRes}"
                Media3Item.fromUri(uri)
            }
            else -> null
        }

        media3Item?.let {
            exoPlayer?.setMediaItem(it)
            exoPlayer?.prepare()
        }
    }

    @OptIn(UnstableApi::class)
    private fun setupVisualizer() {
        val sessionId = exoPlayer?.audioSessionId ?: return
        if (sessionId <= 0 || visualizer != null) return

        try {
            visualizer = Visualizer(sessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(v: Visualizer?, waveform: ByteArray?, samplingRate: Int) {}

                    override fun onFftDataCapture(v: Visualizer?, fft: ByteArray?, samplingRate: Int) {
                        if (fft == null) return
                        val points = listOf(
                            fft[2].toInt().coerceAtLeast(0).toFloat() / 128f,
                            fft[6].toInt().coerceAtLeast(0).toFloat() / 128f,
                            fft[12].toInt().coerceAtLeast(0).toFloat() / 128f,
                            fft[18].toInt().coerceAtLeast(0).toFloat() / 128f,
                            fft[24].toInt().coerceAtLeast(0).toFloat() / 128f,
                            fft[32].toInt().coerceAtLeast(0).toFloat() / 128f
                        ).map { it.coerceIn(0.1f, 1f) }
                        _amplitudes.value = points
                    }
                }, Visualizer.getMaxCaptureRate() / 2, false, true)
            }
        } catch (_: Exception) {
            // Visualizer initialization can fail on some devices or if another app is using it.
            // We swallow this to avoid flooding logs with non-fatal stack traces.
            visualizer = null
        }
    }

    override fun togglePlayPause() {
        if (exoPlayer?.isPlaying == true) {
            exoPlayer?.pause()
        } else {
            exoPlayer?.play()
        }
    }

    override fun seekTo(fraction: Float) {
        val durationMs = exoPlayer?.duration?.takeIf { it > 0 } ?: ((mediaItem?.durationSeconds?.toLong() ?: 0L) * 1000)
        val targetMs = (fraction * durationMs).toLong()
        seekToMs(targetMs)
    }

    override fun seekToMs(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _isPlaybackFinished.value = false
        updateIndices(positionMs)
    }

    private fun updateIndices(positionMs: Long) {
        if (wordTimings.isNotEmpty()) {
            val wordIndex = findCurrentWordIndex(positionMs, wordTimings)
            _currentWordIndex.value = wordIndex
            
            if (wordIndex != -1) {
                val sentenceIndex = storyChunks.indexOfFirst { 
                    wordIndex in it.startWordIndex..it.endWordIndex 
                }
                if (sentenceIndex != -1) {
                    _currentSentenceIndex.value = sentenceIndex
                }
            }
        } else if (storyChunks.isNotEmpty()) {
            // Fallback for legacy audio without word timings
            val durationMs = exoPlayer?.duration?.takeIf { it > 0 } 
                ?: ((mediaItem?.durationSeconds?.toLong() ?: 0L) * 1000)
            val progress = positionMs.toFloat() / durationMs.coerceAtLeast(1L)
            
            // Re-calculate thresholds if needed or use a simple linear heuristic
            val wordCounts = storyChunks.map { it.text.split(Regex("\\s+")).size.coerceAtLeast(1) }
            val totalWords = wordCounts.sum().toFloat()
            var cumulativeWeight = 0f
            val thresholds = wordCounts.map { count ->
                val start = cumulativeWeight / totalWords
                cumulativeWeight += count
                start
            }
            
            val index = thresholds.indexOfLast { it <= progress }.coerceIn(0, storyChunks.size - 1)
            _currentSentenceIndex.value = index
        }
    }

    private fun findCurrentWordIndex(positionMs: Long, timings: List<com.esupplemental.data.remote.model.WordTiming>): Int {
        var low = 0
        var high = timings.size - 1

        while (low <= high) {
            val mid = (low + high) / 2
            val timing = timings[mid]

            when {
                positionMs < timing.startMs -> high = mid - 1
                positionMs > timing.endMs -> low = mid + 1
                else -> return mid
            }
        }
        
        // If not found in a specific word range, return the index of the word that just ended
        // but only if we are before the next word starts.
        return if (high >= 0 && positionMs >= timings[high].endMs) high else -1
    }

    override fun skipForward() {
        val current = exoPlayer?.currentPosition ?: 0L
        val duration =
            exoPlayer?.duration?.takeIf { it > 0 } ?: ((mediaItem?.durationSeconds?.toLong()
                ?: 0L) * 1000L)
        val target = (current + 10_000).coerceAtMost(duration)
        seekToMs(target)
    }

    override fun skipBackward() {
        val current = exoPlayer?.currentPosition ?: 0L
        val target = (current - 10_000).coerceAtLeast(0L)
        seekToMs(target)
    }

    override fun stop() {
        _isPlaying.value = false
        exoPlayer?.pause()
        tickerJob?.cancel()
        try { visualizer?.enabled = false } catch (_: Exception) {}
    }

    override fun release() {
        stop()
        exoPlayer?.release()
        try { 
            visualizer?.enabled = false
            visualizer?.release() 
        } catch (_: Exception) {}
        exoPlayer = null
        visualizer = null
    }

    private fun startTickerJob(type: MediaType) {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            val item = mediaItem ?: return@launch

            while (_isPlaying.value) {
                val currentMs = exoPlayer?.currentPosition ?: 0L
                _currentPositionMs.value = currentMs
                
                val currentSec = (currentMs / 1000).toInt()
                val effectiveDurationMs = exoPlayer?.duration
                    ?.takeIf { it > 0 }
                    ?: (item.durationSeconds * 1000L).coerceAtLeast(1L)
                val effectiveDurationSeconds = (effectiveDurationMs / 1000L).toInt().coerceAtLeast(1)
                _currentPositionSeconds.value = currentSec.coerceAtMost(effectiveDurationSeconds)

                // ── Synchronization ──
                if (type == MediaType.STORY) {
                    updateIndices(currentMs)
                }
                
                val isVisualizerWorking = try { visualizer?.enabled == true } catch (_: Exception) { false }
                
                if (!isVisualizerWorking) {
                    _amplitudes.value = List(6) { (2..8).random().toFloat() / 10f }
                }
                delay(30L.milliseconds) // Faster ticker for word highlighting
            }
        }
    }
}
