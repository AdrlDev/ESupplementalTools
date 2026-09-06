package com.esupplemental.presentation.ui.screens.game.easy.word_master

import android.content.Context
import com.esupplemental.data.model.game.WordItem
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.CopyOnWriteArrayList

import com.esupplemental.domain.utils.FinalData
import com.esupplemental.domain.utils.StoryLoader

/**
 * Word bank for the Word Master game.
 * Extracts dynamic words from story transcripts in assets with story references.
 */
object WordMasterBank {
    
    // Use CopyOnWriteArrayList for thread-safe access
    private val dynamicWords = CopyOnWriteArrayList<WordItem>()

    /** 
     * Returns all available dynamic words. 
     */
    val all: List<WordItem> get() = dynamicWords

    /**
     * Loads unique words from all bundled story assets.
     */
    fun init(context: Context) {
        if (dynamicWords.isNotEmpty()) return // Already loaded

        try {
            val commonStopWords = setOf(
                "about", "after", "again", "also", "because", "before", "being", "could",
                "every", "first", "from", "have", "into", "just", "more", "other", "should",
                "some", "than", "that", "their", "them", "then", "there", "these", "they",
                "this", "through", "very", "were", "what", "when", "where", "which", "while",
                "with", "would", "your", "their"
            )

            val uniqueWords = mutableMapOf<String, WordItem>()

            FinalData.stories.forEach { story ->
                runCatching {
                    val transcript = StoryLoader.load(context, story.transcript)
                    val wordsInStory = transcript.lowercase()
                        .replace(Regex("[^a-z\\s]"), " ")
                        .split(Regex("\\s+"))
                        .filter { it.length in 4..12 && it !in commonStopWords }
                    
                    wordsInStory.distinct().forEach { word ->
                        uniqueWords.putIfAbsent(
                            word,
                            WordItem(
                                id = "dynamic_$word",
                                word = word,
                                emoji = "",
                                sourceTitle = story.title,
                                mediaId = story.id
                            )
                        )
                    }
                }
            }

            dynamicWords.addAll(uniqueWords.values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun forStory(mediaId: String?): List<WordItem> {
        if (mediaId.isNullOrBlank()) return all
        val filtered = all.filter { it.mediaId == mediaId }
        return if (filtered.isNotEmpty()) filtered else all
    }

    fun random(mediaId: String? = null): WordItem {
        val pool = forStory(mediaId)
        return pool.randomOrNull() ?: all.randomOrNull() ?: WordItem("default", "learning", "", "Story Time", mediaId ?: "")
    }
}
