package com.esupplemental.data.local.dao

import androidx.room.*
import androidx.paging.PagingSource
import com.esupplemental.data.local.entity.AudioStoryEntity
import com.esupplemental.data.local.entity.MediaItemEntity
import com.esupplemental.data.local.entity.SongActivityEntity
import com.esupplemental.data.local.entity.StoryActivityEntity
import com.esupplemental.data.model.MediaType
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(items: List<MediaItemEntity>)

    @Query("SELECT * FROM media_items WHERE type = :type")
    fun getMediaByType(type: MediaType): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE type = :type ORDER BY title ASC")
    fun getMediaByTypePaged(type: MediaType): PagingSource<Int, MediaItemEntity>

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getMediaById(id: String): MediaItemEntity?

    @Query("UPDATE media_items SET durationSeconds = :duration WHERE id = :id")
    suspend fun updateMediaDuration(id: String, duration: Int)

    // ── Audio Stories ────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioStory(audioStory: AudioStoryEntity)

    @Query("SELECT * FROM audio_stories WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getAudioStoryByMediaId(mediaId: String): AudioStoryEntity?

    // ── Song activities ───────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongActivities(activities: List<SongActivityEntity>)

    @Query("SELECT * FROM song_activities WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getSongActivity(mediaId: String): SongActivityEntity?

    // ── Story activities ──────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoryActivities(activities: List<StoryActivityEntity>)

    @Query("SELECT * FROM story_activities WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getStoryActivity(mediaId: String): StoryActivityEntity?

    @Query("SELECT * FROM story_activities")
    suspend fun getAllStoryActivities(): List<StoryActivityEntity>

    @Query("SELECT * FROM media_items WHERE type = 'STORY' LIMIT :limit")
    suspend fun getStoriesForGame(limit: Int): List<MediaItemEntity>
}
