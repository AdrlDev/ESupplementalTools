package com.esupplemental.domain.utils

import android.content.Context
import com.esupplemental.data.model.MediaItem
import java.text.BreakIterator
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

data class SpeedTyperChallenge(
    val id: String,
    val mediaId: String,
    val sentence: String
)

/**
 * Sources sentences for the Speed Typer game directly from story assets.
 */
object SpeedTyperBank {
    private val challenges = ConcurrentHashMap<String, List<SpeedTyperChallenge>>()

    fun init(context: Context, stories: List<MediaItem>) {
        if (challenges.isNotEmpty()) return
        
        stories.forEach { story ->
            try {
                val transcript = StoryLoader.load(context, story.transcript)
                val sentences = extractSentences(transcript)
                
                val storyChallenges = sentences.take(5).mapIndexed { index, sentence ->
                    SpeedTyperChallenge(
                        id = AudioCacheKey.fromText("${story.id}_speed_v2_$index", sentence),
                        mediaId = story.id,
                        sentence = sentence
                    )
                }
                challenges[story.id] = storyChallenges
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getChallenges(mediaId: String): List<SpeedTyperChallenge> = 
        challenges[mediaId] ?: emptyList()

    private fun extractSentences(text: String): List<String> {
        val normalized = text
            .replace(Regex("#+\\s*.*"), "")
            .replace(Regex("\\*\\*|__|\\*|_"), "")
            .replace(Regex("\\[(.*?)]\\(.*?\\)"), "$1")
            .trim()

        val iterator = BreakIterator.getSentenceInstance(Locale.US)
        iterator.setText(normalized)
        
        val result = mutableListOf<String>()
        var start = iterator.first()
        var end = iterator.next()
        
        while (end != BreakIterator.DONE) {
            val sentence = normalized.substring(start, end).trim()
            val wordCount = sentence.split(Regex("\\s+")).filter { it.isNotBlank() }.size
            if (wordCount in 4..8 && !sentence.contains("\n")) {
                result.add(sentence)
            }
            start = end
            end = iterator.next()
        }
        
        return if (result.isNotEmpty()) result else listOf("Keep moving forward and never give up.")
    }
}
