package com.esupplemental.domain.utils

import android.content.Context
import com.esupplemental.data.model.MediaItem
import com.esupplemental.presentation.state.game_state.RecallQuestion
import java.text.BreakIterator
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Sources challenging comprehension and recall questions directly from story assets.
 */
object StoryRecallBank {
    private val challenges = ConcurrentHashMap<String, List<RecallQuestion>>()

    fun init(context: Context, stories: List<MediaItem>) {
        if (challenges.isNotEmpty()) return

        stories.forEach { story ->
            try {
                val transcript = StoryLoader.load(context, story.transcript)
                val sentences = extractStoryKeySentences(transcript)
                val storyQuestions = generateStoryQuestions(story, sentences)
                challenges[story.id] = storyQuestions
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getQuestions(mediaId: String): List<RecallQuestion> = 
        challenges[mediaId] ?: emptyList()

    private fun extractStoryKeySentences(text: String): List<String> {
        val clean = text
            .replace(Regex("#+\\s*.*"), "")
            .replace(Regex("\\[(.*?)]\\(.*?\\)"), "$1")
            .replace(Regex("\\*\\*|__|\\*|_"), "")
            .trim()

        val iterator = BreakIterator.getSentenceInstance(Locale.US)
        iterator.setText(clean)

        val sentences = mutableListOf<String>()
        var start = iterator.first()
        var end = iterator.next()

        while (end != BreakIterator.DONE) {
            val sentence = clean.substring(start, end).trim().trim('"', '“', '”')
            if (sentence.length in 30..180 && !sentence.contains("\n")) {
                sentences.add(sentence)
            }
            start = end
            end = iterator.next()
        }
        return sentences
    }

    private fun generateStoryQuestions(story: MediaItem, sentences: List<String>): List<RecallQuestion> {
        val title = story.title
        val moral = story.moral ?: "Working hard and making good choices leads to positive results."
        val s1 = sentences.getOrNull(0) ?: "The journey began with high expectations."
        val s2 = sentences.getOrNull((sentences.size * 0.25).toInt()) ?: "An unexpected challenge arose."
        val s3 = sentences.getOrNull((sentences.size * 0.50).toInt()) ?: "Effort was made to overcome the difficulty."
        val s4 = sentences.getOrNull((sentences.size * 0.75).toInt()) ?: "A critical turning point changed everything."
        val s5 = sentences.getOrNull(sentences.lastIndex) ?: "The situation concluded with valuable lessons learned."

        val q1 = buildQuestion(
            id = AudioCacheKey.fromText("${story.id}_recall_v2_q1", "In \"$title\", how did the story begin?"),
            prompt = "In \"$title\", how did the story begin?",
            correct = s1,
            distractors = listOf(
                "Everything was already resolved from the very start.",
                "The characters immediately gave up without trying.",
                "It was night time and everyone was asleep."
            ),
            targetIndex = 0,
            explanation = "The story introduced the initial setting: \"$s1\""
        )

        val q2 = buildQuestion(
            id = AudioCacheKey.fromText("${story.id}_recall_v2_q2", "What key event happened as the plot developed in \"$title\"?"),
            prompt = "What key event happened as the plot developed in \"$title\"?",
            correct = s2,
            distractors = listOf(
                "A sudden storm destroyed all hope permanently.",
                "The main character decided to move far away.",
                "No events of significance took place."
            ),
            targetIndex = 2,
            explanation = "During the middle of the story: \"$s2\""
        )

        val q3 = buildQuestion(
            id = AudioCacheKey.fromText("${story.id}_recall_v2_q3", "Which action represents the central effort in \"$title\"?"),
            prompt = "Which action represents the central effort in \"$title\"?",
            correct = s3,
            distractors = listOf(
                "Relying entirely on luck and chance.",
                "Waiting for someone else to fix the problem.",
                "Leaving the scene without taking action."
            ),
            targetIndex = 1,
            explanation = "The turning action was: \"$s3\""
        )

        val q4 = buildQuestion(
            id = AudioCacheKey.fromText("${story.id}_recall_v2_q4", "What important occurrence led towards the story's outcome?"),
            prompt = "What important occurrence led towards the story's outcome?",
            correct = s4,
            distractors = listOf(
                "The character forgot why they started.",
                "A stranger took away all the progress.",
                "The story ended abruptly with no explanation."
            ),
            targetIndex = 3,
            explanation = "Leading to the conclusion: \"$s4\""
        )

        val q5 = buildQuestion(
            id = AudioCacheKey.fromText("${story.id}_recall_v2_q5", "What is the key takeaway or message from \"$title\"?"),
            prompt = "What is the key takeaway or message from \"$title\"?",
            correct = moral,
            distractors = listOf(
                "Giving up early is the easiest choice.",
                "Only luck determines success in difficult situations.",
                "Never listen to advice from others."
            ),
            targetIndex = 1,
            explanation = "The moral of $title is: $moral"
        )

        return listOf(q1, q2, q3, q4, q5)
    }

    private fun buildQuestion(
        id: String,
        prompt: String,
        correct: String,
        distractors: List<String>,
        targetIndex: Int,
        explanation: String
    ): RecallQuestion {
        val list = mutableListOf<String>()
        val dists = distractors.toMutableList()
        for (i in 0 until 4) {
            if (i == targetIndex) {
                list.add(correct)
            } else if (dists.isNotEmpty()) {
                list.add(dists.removeAt(0))
            } else {
                list.add("None of the above.")
            }
        }
        return RecallQuestion(
            id = id,
            question = prompt,
            choices = list,
            correctIndex = targetIndex,
            explanation = explanation
        )
    }
}

