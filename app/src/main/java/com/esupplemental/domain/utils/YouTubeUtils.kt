package com.esupplemental.domain.utils

class YouTubeUtils {
    companion object {
        /**
         * Returns the high-quality thumbnail URL for a given YouTube video ID.
         */
        fun getThumbnailUrl(videoId: String): String {
            return "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
        }
    }
}
