package com.esupplemental.domain.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

class Utility {
    companion object {
        fun estimateStoryDuration(transcript: String, wordsPerMinute: Int = 140): Int {
            // Split by whitespace to get total words
            val words = transcript.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
            val wordCount = words.size

            // Convert minutes to total seconds
            val minutes = wordCount.toDouble() / wordsPerMinute
            return (minutes * 60).toInt()
        }

        /**
         * Converts a time string (e.g., "4:36" or "01:20:15") into total seconds.
         * Supports formats: MM:SS, H:MM:SS, HH:MM:SS.
         */
        fun timeToSeconds(time: String): Int {
            val parts = time.split(":").mapNotNull { it.toIntOrNull() }
            var total = 0
            when (parts.size) {
                1 -> total = parts[0] // Just seconds
                2 -> total = (parts[0] * 60) + parts[1] // MM:SS
                3 -> total = (parts[0] * 3600) + (parts[1] * 60) + parts[2] // HH:MM:SS
            }
            return total
        }

        fun getIconForKey(key: String): ImageVector {
            return when (key) {
                "listen_slap" -> Icons.Rounded.TouchApp
                "story_order" -> Icons.Rounded.MenuBook
                "word_bingo" -> Icons.Rounded.GridOn
                "disappearing_text" -> Icons.Rounded.AutoFixHigh
                "two_truths_lie" -> Icons.Rounded.Psychology
                "minimal_pairs" -> Icons.Rounded.Hearing
                "speed_typer" -> Icons.Rounded.Bolt
                "follow_directions" -> Icons.Rounded.CompassCalibration
                "accent_detective" -> Icons.Rounded.RecordVoiceOver
                else -> Icons.Rounded.TouchApp
            }
        }
    }
}
