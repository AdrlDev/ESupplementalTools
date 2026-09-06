package com.esupplemental.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Status of audio story generation.
 */
enum class StoryAudioStatus {
    @SerializedName("READY")
    READY,
    @SerializedName("PROCESSING")
    PROCESSING,
    @SerializedName("FAILED")
    FAILED
}

/**
 * Response body from the audio story generation API.
 */
data class AudioStoryResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("mediaId")
    val mediaId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String?,
    @SerializedName("fileName")
    val fileName: String?,
    @SerializedName("voiceId")
    val voiceId: String?,
    @SerializedName("modelId")
    val modelId: String?,
    @SerializedName("characterCount")
    val characterCount: Int,
    @SerializedName("fileSize")
    val fileSize: Long,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("status")
    val status: StoryAudioStatus,
    @SerializedName("errorMessage")
    val errorMessage: String? = null,
    @SerializedName("words")
    val words: List<WordTiming>? = null
)
