package com.esupplemental.data.local.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.esupplemental.data.local.dao.MediaDao
import com.esupplemental.data.local.entity.StoryActivityEntity
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.mapper.DataMapper.toDomainModel
import com.esupplemental.data.mapper.DataMapper.toEntity
import com.esupplemental.data.model.*
import com.esupplemental.data.remote.AudioStoryApi
import com.esupplemental.data.remote.model.AudioStoryRequest
import com.esupplemental.data.remote.model.StoryAudioStatus
import kotlinx.coroutines.delay
import com.esupplemental.domain.model.game.StoryGameContext
import com.esupplemental.domain.usecases.media.ProcessTranscriptUseCase
import com.esupplemental.domain.utils.ErrorMapper
import com.esupplemental.domain.utils.FinalData
import com.esupplemental.domain.utils.PoemActivityFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import android.media.MediaMetadataRetriever
import com.esupplemental.BuildConfig
import com.esupplemental.data.remote.model.AudioStoryResponse
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

class MediaRepositoryImpl(
    private val mediaDao: MediaDao,
    private val audioStoryApi: AudioStoryApi,
    private val storyAssetReader: (String) -> String = {
        error("A story asset reader is required to seed story activities")
    }
) : MediaRepository {

    private val audioGenerationLocks = ConcurrentHashMap<String, Mutex>()

    // ── Queries ───────────────────────────────────────────────────────────────

    override fun getMediaList(type: MediaType): Flow<List<MediaItem>> =
        mediaDao.getMediaByType(type).map { entities ->
            entities.map { it.toDomainModel() }
        }

    override fun getMediaListPaged(type: MediaType): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            )
        ) { mediaDao.getMediaByTypePaged(type) }.flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }

    override suspend fun getMediaDetail(id: String): MediaItem? =
        mediaDao.getMediaById(id)?.toDomainModel()

    override suspend fun getStoryContext(mediaId: String): StoryGameContext? {
        val mediaItem = getMediaDetail(mediaId) ?: return null
        if (mediaItem.type != MediaType.STORY && mediaItem.type != MediaType.POEM) return null

        val audioStory = mediaDao.getAudioStoryByMediaId(mediaId)?.toDomainModel()
        
        // Resolve transcript text
        val loadedTranscript = try {
            if (mediaItem.transcript.endsWith(".md")) {
                storyAssetReader(mediaItem.transcript)
            } else {
                mediaItem.transcript
            }
        } catch (_: Exception) {
            mediaItem.transcript
        }

        val transcriptText = if (mediaItem.type == MediaType.POEM) {
            PoemActivityFactory.extractTranscript(loadedTranscript)
        } else {
            loadedTranscript
        }

        val chunks = ProcessTranscriptUseCase().invoke(transcriptText)
        val sentences = chunks.map { it.text }
        val words = chunks.flatMap { it.text.split(Regex("\\s+")).filter { w -> w.isNotBlank() } }

        return StoryGameContext(
            mediaId = mediaId,
            title = mediaItem.title,
            transcript = transcriptText,
            sentences = sentences,
            words = words,
            wordTimings = audioStory?.words ?: emptyList(),
            audioUrl = audioStory?.url ?: mediaItem.audioUrl,
            durationSeconds = audioStory?.durationSeconds ?: mediaItem.durationSeconds
        )
    }

    override suspend fun getStoryActivity(mediaId: String): StoryActivity? =
        mediaDao.getStoryActivity(mediaId)?.toDomainModel()

    override suspend fun getSongActivity(mediaId: String): SongActivity? =
        mediaDao.getSongActivity(mediaId)?.toDomainModel()

    override suspend fun getAllStoryActivities(): List<StoryActivity> =
        mediaDao.getAllStoryActivities().map { it.toDomainModel() }

    override suspend fun getStoriesForGame(limit: Int): List<MediaItem> =
        mediaDao.getStoriesForGame(limit).map { it.toDomainModel() }

    override suspend fun generateAudioForText(
        id: String,
        title: String?,
        text: String
    ): Result<AudioStory> {
        val generationLock = audioGenerationLocks.getOrPut(id) { Mutex() }
        return generationLock.withLock {
            generateAudioForTextUnlocked(id, title, text)
        }
    }

    private suspend fun generateAudioForTextUnlocked(
        id: String,
        title: String?,
        text: String
    ): Result<AudioStory> = runCatching {
        // Check local cache first
        val localAudio = mediaDao.getAudioStoryByMediaId(id)
        if (localAudio != null) {
            // Return cache immediately. If it lacks words, the UI will still work (sentence fallback).
            return@runCatching localAudio.toDomainModel()
        }

        val apiKey = BuildConfig.APP_API_KEY
        val request = AudioStoryRequest(id, title ?: text, text)
        val response = audioStoryApi.generateAudioStory(apiKey, request)

        var data: AudioStoryResponse?

        if ((response.isSuccessful || response.code() == 202) && response.body() != null) {
            data = response.body()!!
            if (data.status == StoryAudioStatus.PROCESSING) {
                data = pollForReadyStatus(id, apiKey)
            }
        } else if (response.code() == 409) {
            // 409 Conflict: Audio generation was already initiated or completed on the server for this mediaId.
            val statusResponse = audioStoryApi.getAudioStoryStatus(apiKey, id)
            if (statusResponse.isSuccessful && statusResponse.body() != null) {
                val currentStatus = statusResponse.body()!!
                data = if (currentStatus.status == StoryAudioStatus.PROCESSING) {
                    pollForReadyStatus(id, apiKey)
                } else {
                    currentStatus
                }
            } else {
                val statusError = ErrorMapper.mapHttpCode(statusResponse.code())
                throw Exception("Audio generation conflict ($statusError). Please try again.")
            }
        } else {
            val errorMsg = ErrorMapper.mapHttpCode(response.code())
            throw Exception(errorMsg)
        }

        when (data.status) {
            StoryAudioStatus.READY -> {
                val url = data.url ?: throw Exception("Audio generation returned empty URL")
                // Fetch actual duration from the generated MP3
                val duration = getAudioDuration(url)

                val audioEntity = AudioStory(
                    id = 0, // Generated by Room
                    mediaId = id,
                    remoteId = data.id,
                    title = data.title,
                    url = url,
                    fileName = data.fileName ?: "",
                    voiceId = data.voiceId ?: "",
                    modelId = data.modelId ?: "",
                    characterCount = data.characterCount,
                    fileSize = data.fileSize,
                    durationSeconds = duration,
                    remoteCreatedAt = data.createdAt ?: "",
                    words = data.words ?: emptyList()
                ).toEntity()

                mediaDao.insertAudioStory(audioEntity)

                // Update duration in the main media items table if this mediaId exists there
                mediaDao.updateMediaDuration(id, duration)

                val saved = mediaDao.getAudioStoryByMediaId(id)
                saved?.toDomainModel() ?: throw Exception("Failed to save audio story to local database")
            }
            StoryAudioStatus.FAILED -> {
                throw Exception(data.errorMessage ?: "Audio generation failed on server")
            }
            else -> {
                throw Exception("Audio generation timed out or returned invalid state")
            }
        }
    }.recoverCatching { throwable ->
        // Map any exception (Network, HTTP, DB) to human readable string
        val message = ErrorMapper.map(throwable)
        throw Exception(message)
    }

    /**
     * Polls the status endpoint until READY or FAILED, with a timeout.
     */
    private suspend fun pollForReadyStatus(
        mediaId: String,
        apiKey: String
    ): AudioStoryResponse {
        val maxAttempts = 60 // ~3 minutes with 3s delay
        val delayMs = 3000L
        var consecutiveErrors = 0

        repeat(maxAttempts) { _ ->
            delay(delayMs.milliseconds)
            val response = audioStoryApi.getAudioStoryStatus(apiKey, mediaId)
            if (response.isSuccessful && response.body() != null) {
                consecutiveErrors = 0
                val data = response.body()!!
                if (data.status != StoryAudioStatus.PROCESSING) {
                    return data
                }
            } else {
                consecutiveErrors++
                if (consecutiveErrors >= 3) {
                    val errorMsg = ErrorMapper.mapHttpCode(response.code())
                    throw Exception("Server returned error ($errorMsg) during status check.")
                }
            }
        }
        
        throw Exception("Story audio generation is taking longer than expected.")
    }

    /**
     * Helper to fetch audio duration from a URL using MediaMetadataRetriever.
     */
    private suspend fun getAudioDuration(url: String): Int = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(url, HashMap<String, String>())
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = time?.toLong() ?: 0L
            (durationMs / 1000).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ── Seeding ───────────────────────────────────────────────────────────────

    /**
     * Inserts all seed data into the database.
     * Uses REPLACE strategy so calling this multiple times is safe (idempotent).
     */
    override suspend fun seedInitialData() {
        seedMedia()
        seedSongActivities()
        seedStoryActivities()
    }

    /** Seeds songs + stories into the media_items table. */
    private suspend fun seedMedia() {
        val entities = (FinalData.songs + FinalData.stories + FinalData.poems).map { it.toEntity() }
        mediaDao.insertMedia(entities)
    }

    /** Seeds all story activities using the shared DataMapper. */
    private suspend fun seedStoryActivities() = withContext(Dispatchers.IO) {
        val entities: List<StoryActivityEntity> =
            (FinalData.storyActivities(storyAssetReader) + FinalData.poemActivities(storyAssetReader))
                .values.map { it.toEntity() }
        mediaDao.insertStoryActivities(entities)
    }

    /** Seeds song challenges parsed from the question and answer Markdown assets. */
    private suspend fun seedSongActivities() = withContext(Dispatchers.IO) {
        val entities = FinalData.songActivities(storyAssetReader).map { it.toEntity() }
        mediaDao.insertSongActivities(entities)
    }
}
