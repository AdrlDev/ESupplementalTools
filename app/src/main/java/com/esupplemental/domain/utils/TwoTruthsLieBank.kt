package com.esupplemental.domain.utils

import android.content.Context
import com.esupplemental.data.model.game.Statement
import com.esupplemental.data.model.game.TwoTruthsLieQuestion
import java.text.BreakIterator
import java.util.Locale

/** Builds stable 2 Truths 1 Lie rounds directly from the bundled story assets. */
object TwoTruthsLieBank {
    private const val QUESTION_COUNT = 5
    private const val MIN_WORDS = 6
    private const val MAX_WORDS = 18

    private val blankLine = Regex("\\n\\s*\\n")
    private val stageDirection = Regex("\\[[^]]+]\\s*")
    private val brokenWord = Regex("(?<=\\p{L})-\\s+(?=\\p{Ll})")
    private val whitespace = Regex("\\s+")

    @Volatile
    private var cachedQuestions: List<TwoTruthsLieQuestion> = emptyList()

    fun load(context: Context): List<TwoTruthsLieQuestion> {
        cachedQuestions.takeIf { it.isNotEmpty() }?.let { return it }

        return synchronized(this) {
            cachedQuestions.takeIf { it.isNotEmpty() } ?: buildQuestions(
                FinalData.stories.map { story ->
                    StoryContent(
                        id = story.id,
                        title = story.title,
                        markdown = StoryLoader.load(context, story.transcript)
                    )
                }
            ).also { cachedQuestions = it }
        }
    }

    fun audioTitle(question: TwoTruthsLieQuestion): String =
        "${question.sourceTitle} — 2 Truths 1 Lie"

    internal fun buildQuestions(stories: List<StoryContent>): List<TwoTruthsLieQuestion> {
        val sources = stories.mapNotNull { story ->
            val sentences = extractSentences(story.markdown)
            story.takeIf { sentences.size >= 2 }?.let {
                StorySentences(story.id, story.title, sentences)
            }
        }
        if (sources.size < 2) return emptyList()

        return sources.take(QUESTION_COUNT).mapIndexed { index, source ->
            val firstIndex = Math.floorMod(source.id.hashCode(), source.sentences.size)
            val secondIndex = generateSequence((firstIndex + 1) % source.sentences.size) {
                (it + 1) % source.sentences.size
            }.first { it != firstIndex }
            val truths = listOf(
                source.sentences[firstIndex],
                source.sentences[secondIndex]
            )

            // The third option also comes from a bundled story, but not from the
            // two-sentence context the learner hears for this round.
            val distractorSource = sources[(index + 1) % sources.size]
            val distractorIndex = Math.floorMod(
                "${source.id}:${distractorSource.id}".hashCode(),
                distractorSource.sentences.size
            )
            val distractor = distractorSource.sentences[distractorIndex]
            val audioText = truths.joinToString(" ")

            TwoTruthsLieQuestion(
                id = AudioCacheKey.fromText(
                    prefix = "${source.id}_two_truths_lie",
                    text = audioText
                ),
                sourceTitle = source.title,
                audioText = audioText,
                statements = listOf(
                    Statement(truths[0], true),
                    Statement(truths[1], true),
                    Statement(distractor, false)
                )
            )
        }
    }

    private fun extractSentences(markdown: String): List<String> {
        val sentences = markdown
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

        return sentences.filter { sentence ->
            sentence.wordCount() in MIN_WORDS..MAX_WORDS && sentence.length in 30..140
        }.ifEmpty {
            sentences.filter { sentence ->
                sentence.wordCount() in 5..22 && sentence.length in 25..170
            }
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

    internal data class StoryContent(
        val id: String,
        val title: String,
        val markdown: String
    )

    private data class StorySentences(
        val id: String,
        val title: String,
        val sentences: List<String>
    )
}
