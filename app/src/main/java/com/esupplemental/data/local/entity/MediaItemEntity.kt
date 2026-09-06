package com.esupplemental.data.local.entity

import androidx.room.*
import com.esupplemental.data.model.MediaType
import com.esupplemental.data.model.StoryCategory

@Entity(
    tableName = "media_items",
    indices = [Index(value = ["type", "title"])]
)
data class MediaItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleTl: String? = null,
    val singer: String? = null,
    val category: StoryCategory? = null,
    val type: MediaType,
    val durationSeconds: Int,
    val thumbnailRes: Int = 0,
    val thumbnailUrl: String? = null,
    val audioRes: Int = 0,
    val audioUrl: String? = null,
    val transcript: String,
    val transcriptTl: String? = null,
    val moral: String? = null,
    val moralTl: String? = null
)
