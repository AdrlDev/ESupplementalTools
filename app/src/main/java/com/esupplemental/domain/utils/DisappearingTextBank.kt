package com.esupplemental.domain.utils

import android.content.Context
import com.esupplemental.data.model.game.DisappearingTextQuestion
import java.text.BreakIterator
import java.util.Locale

/** Builds stable Disappearing Text questions directly from the bundled story assets. */
object DisappearingTextBank {
    private const val MIN_WORDS = 6
    private const val MAX_WORDS = 12

    private val blankLine = Regex("\\n\\s*\\n")
    private val stageDirection = Regex("\\[[^]]+]\\s*")
    private val brokenWord = Regex("(?<=\\p{L})-\\s+(?=\\p{Ll})")
    private val whitespace = Regex("\\s+")

    @Volatile
    private var cachedQuestions: List<DisappearingTextQuestion> = emptyList()

    fun load(context: Context): List<DisappearingTextQuestion> {
        cachedQuestions.takeIf { it.isNotEmpty() }?.let { return it }

        return synchronized(this) {
            cachedQuestions.takeIf { it.isNotEmpty() } ?: FinalData.stories
                .mapNotNull { story ->
                    runCatching {
                        val markdown = StoryLoader.load(context, story.transcript)
                        val candidates = extractSentences(markdown)
                        if (candidates.isEmpty()) return@runCatching null

                        // Select a deterministic point from each story so all rounds do not use
                        // only opening sentences, while still keeping IDs stable between launches.
                        val candidateIndex = Math.floorMod(story.id.hashCode(), candidates.size)
                        val sentence = candidates[candidateIndex]

                        DisappearingTextQuestion(
                            id = AudioCacheKey.fromText(
                                prefix = "${story.id}_disappearing_text",
                                text = sentence
                            ),
                            sentence = sentence,
                            sourceTitle = story.title
                        )
                    }.getOrNull()
                }
                .also { cachedQuestions = it }
        }
    }

    private fun extractSentences(markdown: String): List<String> {
        val allSentences = markdown
            .replace("\r\n", "\n")
            .split(blankLine)
            .asSequence()
            .map(::cleanParagraph)
            .filter { it.isNotBlank() }
            .flatMap(::sentences)
            .map { it.trim().trim('"', '“', '”') }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()

        val preferred = allSentences
            .filter { sentence -> sentence.wordCount() in MIN_WORDS..MAX_WORDS }
            .filter { it.length in 25..100 }

        return preferred.ifEmpty {
            allSentences
                .filter { sentence -> sentence.wordCount() in 5..14 }
                .filter { it.length in 20..120 }
        }
    }

    private fun cleanParagraph(paragraph: String): String {
        if (paragraph.trimStart().startsWith("#")) return ""

        return paragraph
            .lineSequence()
            .joinToString(" ") { it.trim() }
            .replace(brokenWord, "")
            .replace(stageDirection, "")
            .replace(whitespace, " ")
            .trim()
    }

    private fun sentences(paragraph: String): Sequence<String> = sequence {
        val iterator = BreakIterator.getSentenceInstance(Locale.US)
        iterator.setText(paragraph)
        var start = iterator.first()
        var end = iterator.next()
        while (end != BreakIterator.DONE) {
            yield(paragraph.substring(start, end).trim())
            start = end
            end = iterator.next()
        }
    }

    private fun String.wordCount(): Int = split(whitespace).count { it.isNotBlank() }
}
