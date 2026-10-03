package com.esupplemental.data.local.repository

import androidx.paging.PagingData
import com.esupplemental.data.model.AudioStory
import com.esupplemental.data.model.MediaItem
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.StoryActivity
import com.esupplemental.data.model.SongActivity
import kotlinx.coroutines.flow.Flow

import com.esupplemental.domain.model.game.StoryGameContext

interface MediaRepository {
    fun getMediaList(type: MediaType): Flow<List<MediaItem>>
    fun getMediaListPaged(type: MediaType): Flow<PagingData<MediaItem>>
    suspend fun getMediaDetail(id: String): MediaItem?
    suspend fun getStoryContext(mediaId: String): StoryGameContext?
    suspend fun getStoryActivity(mediaId: String): StoryActivity?
    suspend fun getSongActivity(mediaId: String): SongActivity?
    suspend fun getAllStoryActivities(): List<StoryActivity>
    suspend fun getStoriesForGame(limit: Int): List<MediaItem>
    suspend fun seedInitialData() // Used to inject your list below

    /**
     * Generates or retrieves cached audio for the given text.
     * The [id] should be a unique identifier for the text (e.g., question ID or event ID).
     */
    suspend fun generateAudioForText(
        id: String,
        title: String? = null,
        text: String
    ): Result<AudioStory>
}
