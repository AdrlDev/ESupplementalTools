package com.esupplemental.domain.utils

import com.esupplemental.data.model.MultipleChoiceQuestion
import com.esupplemental.data.model.StoryActivity
import com.esupplemental.data.model.game.StoryEvent
import java.text.BreakIterator
import java.util.Locale
import kotlin.math.roundToInt

/** Builds game activities directly from a story's Markdown asset. */
object StoryActivityFactory {
    private const val EVENT_COUNT = 5
    private const val MAX_EVENT_LENGTH = 220
    private val blankLine = Regex("\\n\\s*\\n")
    private val stageDirection = Regex("\\[[^]]+]\\s*")
    private val brokenWord = Regex("(?<=\\p{L})-\\s+(?=\\p{Ll})")
    private val whitespace = Regex("\\s+")

    fun create(mediaId: String, title: String, markdown: String): StoryActivity {
        val descriptions = extractEventDescriptions(markdown)
        require(descriptions.size >= EVENT_COUNT) {
            "Story asset for $mediaId does not contain enough narrative content"
        }

        val events = descriptions.mapIndexed { index, description ->
            StoryEvent(
                id = AudioCacheKey.fromText(
                    prefix = "${mediaId}_event_${index + 1}",
                    text = description
                ),
                description = description,
                correctOrder = index + 1
            )
        }

        return StoryActivity(
            mediaId = mediaId,
            reorderEvents = events,
            multipleChoiceQuestions = buildQuestions(mediaId, title, events),
            openEnded = emptyList()
        )
    }

    private fun extractEventDescriptions(markdown: String): List<String> {
        val candidates = markdown
            .replace("\r\n", "\n")
            .split(blankLine)
            .asSequence()
            .map(::cleanParagraph)
            .filter { it.length >= 40 }
            .mapNotNull(::firstMeaningfulSentence)
            .distinct()
            .toList()

        if (candidates.size <= EVENT_COUNT) return candidates

        return List(EVENT_COUNT) { index ->
            val position = (
                index * (candidates.lastIndex.toDouble() / (EVENT_COUNT - 1))
                ).roundToInt()
            candidates[position]
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

    private fun firstMeaningfulSentence(paragraph: String): String? {
        val iterator = BreakIterator.getSentenceInstance(Locale.US)
        iterator.setText(paragraph)

        var start = iterator.first()
        var end = iterator.next()
        while (end != BreakIterator.DONE) {
            val sentence = paragraph.substring(start, end).trim().trim('"', '“', '”')
            if (sentence.length >= 35) return sentence.limitLength()
            start = end
            end = iterator.next()
        }
        return null
    }

    private fun String.limitLength(): String {
        if (length <= MAX_EVENT_LENGTH) return this
        val shortened = take(MAX_EVENT_LENGTH - 1)
        val wordBoundary = shortened.lastIndexOf(' ').takeIf { it > 0 } ?: shortened.lastIndex
        return shortened.take(wordBoundary).trimEnd(',', ';', ':', ' ') + "…"
    }

    private fun buildQuestions(
        mediaId: String,
        title: String,
        events: List<StoryEvent>
    ): List<MultipleChoiceQuestion> {
        val specs = listOf(
            QuestionSpec(promptEventIndex = 0, correctEventIndex = 1),
            QuestionSpec(promptEventIndex = 2, correctEventIndex = 3),
            QuestionSpec(promptEventIndex = null, correctEventIndex = events.lastIndex)
        )

        return specs.mapIndexed { questionIndex, spec ->
            val prompt = spec.promptEventIndex?.let { eventIndex ->
                "In \"$title\", what happened immediately after this event?\n" +
                    "\"${events[eventIndex].description}\""
            } ?: "Which event happened last in \"$title\"?"

            val excludedIndices = listOfNotNull(
                spec.promptEventIndex,
                spec.correctEventIndex
            ).toSet()
            val distractors = events.indices
                .filterNot { it in excludedIndices }
                .take(3)
                .map { events[it].description }
            val unrotatedChoices = listOf(events[spec.correctEventIndex].description) + distractors
            val rotation = (questionIndex + 1) % unrotatedChoices.size
            val choices = unrotatedChoices.drop(rotation) + unrotatedChoices.take(rotation)

            MultipleChoiceQuestion(
                id = AudioCacheKey.fromText(
                    prefix = "${mediaId}_question_${questionIndex + 1}",
                    text = prompt
                ),
                question = prompt,
                choices = choices,
                correctAnswerIndex = choices.indexOf(events[spec.correctEventIndex].description)
            )
        }
    }

    private data class QuestionSpec(
        val promptEventIndex: Int?,
        val correctEventIndex: Int
    )
}
