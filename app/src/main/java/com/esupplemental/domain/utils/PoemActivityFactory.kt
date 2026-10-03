package com.esupplemental.domain.utils

import com.esupplemental.data.model.MultipleChoiceQuestion
import com.esupplemental.data.model.StoryActivity

/** Parses poem quizzes into the existing Story exercise representation. */
object PoemActivityFactory {
    private val questionHeading = Regex("^###\\s+(\\d+)\\.\\s+(.+)$")
    private val option = Regex("^-\\s*([A-D])\\.\\s+(.+)$")
    private val answer = Regex("^Answer:\\s*([A-D]|True|False)\\s*$", RegexOption.IGNORE_CASE)

    fun create(mediaId: String, markdown: String): StoryActivity {
        val normalizedMarkdown = markdown.replace("\r\n", "\n")
        val questions = buildList {
            normalizedMarkdown.substringAfter("\n## Multiple Choice", "")
                .let { if (it.isNotEmpty()) addAll(parseQuestions(mediaId, it, false)) }
            normalizedMarkdown.substringAfter("\n## True or False", "")
                .let { if (it.isNotEmpty()) addAll(parseQuestions(mediaId, it, true)) }
        }
        require(questions.isNotEmpty()) { "Poem asset for $mediaId has no exercise questions" }
        return StoryActivity(
            mediaId = mediaId,
            reorderEvents = emptyList(),
            multipleChoiceQuestions = questions,
            openEnded = emptyList()
        )
    }

    /** Returns only the spoken poem body, excluding quiz headings and answer keys. */
    fun extractTranscript(markdown: String): String {
        val poemSection = markdown.substringAfter("## Poem", markdown)
            .substringBefore("## Multiple Choice")
        return poemSection.lines()
            .filterNot {
                it.trimStart().startsWith("#") ||
                    it.trim().startsWith("Author:") ||
                    it.trimStart().startsWith("Comprehension Quiz")
            }
            .joinToString("\n")
            .trim()
    }

    private fun parseQuestions(mediaId: String, section: String, trueFalse: Boolean): List<MultipleChoiceQuestion> {
        val lines = section.lines().map(String::trim).filter(String::isNotEmpty)
        val result = mutableListOf<MultipleChoiceQuestion>()
        var index = 0
        while (index < lines.size) {
            val heading = questionHeading.matchEntire(lines[index])
            if (heading == null) {
                index++
                continue
            }
            val question = repairQuestion(heading.groupValues[2])
            index++
            val choices = mutableListOf<String>()
            var correct: String? = null
            while (index < lines.size && questionHeading.matchEntire(lines[index]) == null) {
                option.matchEntire(lines[index])?.let { choices += repairChoice(it.groupValues[2]) }
                answer.matchEntire(lines[index])?.let { correct = it.groupValues[1] }
                index++
            }
            val normalizedChoices = if (trueFalse) listOf("True", "False") else choices
            val correctIndex = if (trueFalse) {
                if (correct.equals("True", ignoreCase = true)) 0 else 1
            } else {
                (correct?.firstOrNull()?.uppercaseChar()?.code?.minus('A'.code) ?: -1)
                    .takeIf { it in normalizedChoices.indices } ?: continue
            }
            result += MultipleChoiceQuestion(
                id = AudioCacheKey.fromText("${mediaId}_question_${result.size + 1}", question),
                question = question,
                choices = normalizedChoices,
                correctAnswerIndex = correctIndex
            )
        }
        return result
    }

    // A few source paragraphs ran the next numbered question into the preceding
    // option. The imported Markdown keeps the source wording, while this small
    // boundary repair restores the intended question/choice split for parsing.
    private fun repairQuestion(value: String): String =
        Regex("\\b[2-5]\\.\\s+(.+)$").find(value)?.groupValues?.get(1) ?: value

    private fun repairChoice(value: String): String =
        value.replace(Regex("\\s+[2-5]\\.\\s+.*$"), "").trim()
}
