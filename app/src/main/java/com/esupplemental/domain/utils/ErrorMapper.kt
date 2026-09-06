package com.esupplemental.domain.utils

import androidx.media3.common.PlaybackException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Utility to map various throwables and error codes to human-readable messages.
 */
object ErrorMapper {

    fun map(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> "No internet connection. Please check your network."
            is SocketTimeoutException -> "The server took too long to respond. Please try again."
            is IOException -> "Network error. Please check your connection."
            is HttpException -> mapHttpCode(throwable.code())
            is PlaybackException -> mapPlaybackError(throwable.errorCode)
            else -> throwable.localizedMessage ?: "An unexpected error occurred."
        }
    }

    fun mapHttpCode(code: Int): String {
        return when (code) {
            401 -> "Unauthorized. Please check your API key."
            403 -> "Access forbidden. You might have reached your limit."
            404 -> "Resource not found on the server."
            429 -> "Too many requests. Please wait a moment."
            in 500..599 -> "Server error. We're working on it."
            else -> "API Error ($code). Please try again later."
        }
    }

    private fun mapPlaybackError(errorCode: Int): String {
        return when (errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "Connection failed. Check your internet."
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> "Audio file not found."
            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> "Could not initialize audio decoder."
            PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW -> "Playback is behind the live window."
            else -> "Playback error (Code: $errorCode)"
        }
    }
}
