package com.esupplemental.domain.utils

import android.content.Context
import com.esupplemental.data.model.game.MinimalPairQuestion
import java.util.Locale

/**
 * Curated phonetic minimal pairs filtered against words that really occur in the
 * bundled story assets. The catalog preserves pronunciation validity while the
 * asset filter keeps game vocabulary tied to the learner's reading content.
 */
object MinimalPairsBank {
    private val wordPattern = Regex("[a-z]+")

    private val curatedPairs = listOf(
        "ship" to "sheep",
        "cat" to "cut",
        "man" to "men",
        "light" to "right",
        "glass" to "grass",
        "tree" to "three",
        "free" to "three",
        "day" to "say",
        "boat" to "coat",
        "fly" to "fry",
        "made" to "make",
        "fine" to "five",
        "save" to "safe",
        "life" to "like",
        "tin" to "ten",
        "look" to "luck",
        "not" to "nut",
        "town" to "down",
        "cold" to "gold",
        "see" to "she"
    )

    @Volatile
    private var cachedQuestions: List<MinimalPairQuestion> = emptyList()

    fun load(context: Context): List<MinimalPairQuestion> {
        cachedQuestions.takeIf { it.isNotEmpty() }?.let { return it }

        return synchronized(this) {
            cachedQuestions.takeIf { it.isNotEmpty() } ?: run {
                val storyWords = FinalData.stories
                    .asSequence()
                    .flatMap { story ->
                        val markdown = StoryLoader.load(context, story.transcript)
                        wordPattern.findAll(markdown.lowercase(Locale.US))
                            .map { match -> match.value }
                    }
                    .toSet()

                curatedPairs
                    .asSequence()
                    .filter { (wordA, wordB) -> wordA in storyWords && wordB in storyWords }
                    .map { (wordA, wordB) ->
                        MinimalPairQuestion(
                            id = AudioCacheKey.fromText(
                                prefix = "minimal_pair",
                                text = "$wordA|$wordB"
                            ),
                            wordA = wordA,
                            wordB = wordB,
                            correctWord = wordA
                        )
                    }
                    .toList()
                    .also { cachedQuestions = it }
            }
        }
    }

    fun audioId(word: String): String {
        val normalizedWord = word.lowercase(Locale.US)
        // Word Master/Bingo already use this ID shape for 5+ letter asset words.
        // Sharing it prevents purchasing a second MP3 for identical spoken text.
        return if (normalizedWord.length > 4) {
            "dynamic_$normalizedWord"
        } else {
            AudioCacheKey.fromText("minimal_pair_word", normalizedWord)
        }
    }

    fun spokenWords(questions: List<MinimalPairQuestion>): List<String> =
        questions
            .flatMap { question -> listOf(question.wordA, question.wordB) }
            .distinct()
}
