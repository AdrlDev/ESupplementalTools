package com.esupplemental.data.remote

import com.esupplemental.data.remote.model.AudioStoryRequest
import com.esupplemental.data.remote.model.AudioStoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit interface for Audio Story API.
 */
interface AudioStoryApi {

    @POST("api/v1/audio/stories")
    suspend fun generateAudioStory(
        @Header("X-App-Key") apiKey: String,
        @Body request: AudioStoryRequest
    ): Response<AudioStoryResponse>

    @GET("api/v1/audio/stories/{mediaId}")
    suspend fun getAudioStoryStatus(
        @Header("X-App-Key") apiKey: String,
        @Path("mediaId") mediaId: String
    ): Response<AudioStoryResponse>

    companion object {
        const val BASE_URL = "https://aeserver.aesprt.com/"
    }
}
