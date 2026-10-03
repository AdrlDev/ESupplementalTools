package com.esupplemental.domain.utils

import com.esupplemental.data.model.FillBlankItem
import com.esupplemental.data.model.MultipleChoiceItem
import com.esupplemental.data.model.SongActivity

/** Builds song challenges from the numbered Markdown question and answer assets. */
object SongActivityFactory {
    private const val MESSAGE_HEADER = "## What's the Message?"
    private val numberedBlocks = Regex("""(?ms)^\s*\d+\.\s+(.+?)(?=^\s*\d+\.\s+|\z)""")
    private val markdownEmphasis = Regex("""\*\*(.*?)\*\*""")
    // Some assets use a single underscore while others use two or more.
    private val blankMarkers = Regex("_+")
    private val whitespace = Regex("\\s+")

    fun create(mediaId: String, questionsMarkdown: String, answersMarkdown: String): SongActivity {
        val questionSections = sections(questionsMarkdown)
        val answerSections = sections(answersMarkdown)
        val fillQuestions = numberedBlocks.findAll(questionSections.first)
            .map { clean(it.groupValues[1]) }
            .toList()
        val fillAnswers = numberedBlocks.findAll(answerSections.first)
            .map { clean(it.groupValues[1]) }
            .toList()

        require(fillQuestions.isNotEmpty()) { "Song asset for $mediaId has no fill-in questions" }
        val fillBlanks = buildList {
            var answerIndex = 0
            fillQuestions.forEach { question ->
                val markers = blankMarkers.findAll(question).toList()
                if (markers.isEmpty()) return@forEach
                markers.indices.forEach { targetMarker ->
                    require(answerIndex < fillAnswers.size) {
                        "Song asset for $mediaId has fewer answers than blanks"
                    }
                    var markerIndex = 0
                    val sentence = blankMarkers.replace(question) {
                        val replacement = if (markerIndex == targetMarker) {
                            "___"
                        } else {
                            fillAnswers[answerIndex + markerIndex]
                        }
                        markerIndex++
                        replacement
                    }
                    add(
                        FillBlankItem(
                            id = "${mediaId}_fill_${size + 1}",
                            sentence = sentence,
                            answer = fillAnswers[answerIndex]
                        )
                    )
                    answerIndex++
                }
            }
            require(answerIndex == fillAnswers.size) {
                "Song asset for $mediaId has answers that do not match its blanks"
            }
        }

        val messagePrompt = numberedBlocks.findAll(questionSections.second)
            .firstOrNull()?.groupValues?.get(1)?.let(::clean)
            ?: "What is the main message of this song?"
        val messageAnswer = numberedBlocks.findAll(answerSections.second)
            .firstOrNull()?.groupValues?.get(1)?.let(::clean)
            ?: "The song encourages us to learn from its message."

        return SongActivity(
            mediaId = mediaId,
            fillBlanks = fillBlanks,
            messageQuestion = MultipleChoiceItem(
                id = "${mediaId}_message",
                question = messagePrompt,
                options = listOf(
                    messageAnswer,
                    "The song is mainly about winning a competition.",
                    "The singer wants to forget everyone who helped.",
                    "The song gives instructions for a game."
                ),
                correctIndex = 0
            )
        )
    }

    private fun sections(markdown: String): Pair<String, String> {
        val normalized = markdown.replace("\r\n", "\n")
        val parts = normalized.split(MESSAGE_HEADER, limit = 2)
        return parts.first() to parts.getOrElse(1) { "" }
    }

    private fun clean(value: String): String = value
        .replace(markdownEmphasis, "$1")
        .lineSequence()
        .joinToString(" ") { it.trim() }
        .replace(whitespace, " ")
        .trim()
}
