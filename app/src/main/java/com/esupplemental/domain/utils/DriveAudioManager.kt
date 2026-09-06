package com.esupplemental.domain.utils

class DriveAudioManager {

    companion object {
        /**
         * Extracts the file ID from a Google Drive sharing link.
         */
        fun extractFileId(url: String?): String? {
            if (url.isNullOrBlank()) return null
            val pattern = "(?:/file/d/|id=)([^/&?]+)"
            val compiledPattern = java.util.regex.Pattern.compile(pattern)
            val matcher = compiledPattern.matcher(url)
            return if (matcher.find()) matcher.group(1) else null
        }

        /**
         * Converts a Google Drive sharing link to a direct streaming URL.
         */
        fun getDirectStreamUrl(url: String?): String? {
            val fileId = extractFileId(url) ?: return url
            return "https://drive.google.com/uc?export=download&id=$fileId"
        }
    }
}
