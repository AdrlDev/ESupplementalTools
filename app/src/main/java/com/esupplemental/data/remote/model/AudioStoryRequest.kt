package com.esupplemental.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Request body for generating an audio story.
 */
data class AudioStoryRequest(
    @SerializedName("mediaId")
    val mediaId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("transcript")
    val transcript: String
)
