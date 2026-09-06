package com.esupplemental.domain.usecases.media

import android.app.Application
import android.util.Log
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.model.MediaType
import com.esupplemental.domain.utils.CharacterQuestBank
import com.esupplemental.domain.utils.FollowDirectionsBank
import com.esupplemental.domain.utils.SpeedTyperBank
import com.esupplemental.domain.utils.StoryRecallBank
import com.esupplemental.domain.utils.StoryLoader
import com.esupplemental.domain.utils.StoryOrderSummary
import com.esupplemental.domain.utils.DisappearingTextBank
import com.esupplemental.domain.utils.MinimalPairsBank
import com.esupplemental.domain.utils.TwoTruthsLieBank
import com.esupplemental.presentation.ui.screens.game.easy.word_master.WordMasterBank
import kotlinx.coroutines.flow.first

class SyncInitialMediaUseCase(
    private val repository: MediaRepository,
    private val application: Application
) {
    /**
     * Seeds initial data.
     * Audio prefetching is handled separately by WorkManager after this completes.
     */
    suspend operator fun invoke() {
        repository.seedInitialData()
    }

    /**
     * Performs audio pre-fetching for story activities and audio-backed game content.
     * Called by a WorkManager worker so it can continue outside the UI lifecycle.
     */
    suspend fun prefetchAllStoryAudio() {
        // 1. Prefetch each asset-backed Disappearing Text sentence first so a fresh
        // install does not wait for the larger activity and vocabulary queues.
        // Gameplay uses these same text-hash IDs, so each MP3 URL is reused.
        val disappearingTextQuestions = DisappearingTextBank.load(application)
        disappearingTextQuestions.forEach { question ->
            repository.generateAudioForText(
                id = question.id,
                title = "Disappearing Text",
                text = question.sentence
            ).getOrThrow()
        }

        // 2. Prefetch the distinct story-derived words used by Minimal Pairs.
        // The word-level IDs are shared with gameplay and cached after generation.
        val minimalPairQuestions = MinimalPairsBank.load(application)
        MinimalPairsBank.spokenWords(minimalPairQuestions).forEach { word ->
            repository.generateAudioForText(
                id = MinimalPairsBank.audioId(word),
                title = word,
                text = word
            ).getOrThrow()
        }

        // 3. Prefetch the five story contexts used by 2 Truths 1 Lie. IDs include
        // a text hash, so unchanged contexts reuse their stored MP3 URL forever.
        val twoTruthsQuestions = TwoTruthsLieBank.load(application)
        twoTruthsQuestions.forEach { question ->
            repository.generateAudioForText(
                id = question.id,
                title = TwoTruthsLieBank.audioTitle(question),
                text = question.audioText
            ).getOrThrow()
        }

        // 4. Prefetch Story Activity Audio (All activities)
        val activities = repository.getAllStoryActivities()
        val allStories = repository.getMediaList(MediaType.STORY).first()

        // Initialize game banks from story assets
        SpeedTyperBank.init(application, allStories)
        FollowDirectionsBank.init(application, allStories)
        StoryRecallBank.init(application, allStories)

        activities.forEach { activity ->
            // Prefetch Quiz Questions (Standard)
            activity.multipleChoiceQuestions.forEach { q ->
                runCatching { repository.generateAudioForText(q.id, null, q.question) }
            }
            // Prefetch Quiz Events
            activity.reorderEvents.forEach { e ->
                runCatching { repository.generateAudioForText(e.id, null, e.description) }
            }
        }

        // 5. Prefetch Game Vocabulary (Word Master)
        WordMasterBank.init(application)
        val gameWords = WordMasterBank.all
        gameWords.forEach { wordItem ->
            runCatching { repository.generateAudioForText(wordItem.id, null, wordItem.word) }
        }

        // 6. Prefetch Character Quest dialogue questions
        val charQuestQuestions = CharacterQuestBank.load(application)
        charQuestQuestions.forEach { question ->
            runCatching {
                repository.generateAudioForText(
                    id = question.id,
                    title = "${question.storyTitle} — Who Said It?",
                    text = question.audioText
                )
            }
        }

        // 6b. Prefetch the summarized Story Order narration used by the game.
        // The game uses this same ID, so opening it reads the cached MP3.
        val gameStories = repository.getStoriesForGame(5)
        gameStories.forEach { story ->
            val activity = repository.getStoryActivity(story.id)
            val summary = activity
                ?.let { StoryOrderSummary.narration(it.reorderEvents) }
                .orEmpty()

            if (summary.isNotBlank()) {
                runCatching {
                    repository.generateAudioForText(
                        id = StoryOrderSummary.audioId(story.id, summary),
                        title = StoryOrderSummary.audioTitle(story.title),
                        text = summary
                    )
                }
            }
        }

        // 7. Prefetch Full Story Audio (All stories)
        allStories.forEach { story ->
            try {
                // story.transcript is the path to the .md file
                val fullTranscript = StoryLoader.load(application, story.transcript)
                repository.generateAudioForText(story.id, story.title, fullTranscript)
                // 4b. Prefetch HARD Game: Speed Typer
                SpeedTyperBank.getChallenges(story.id).forEach { challenge ->
                    runCatching {
                        repository.generateAudioForText(
                            id = challenge.id,
                            title = "Speed Typer",
                            text = challenge.sentence
                        ).getOrThrow()
                    }.onFailure { throwable ->
                        Log.e(
                            "AudioPrefetch",
                            "Failed Speed Typer audio: ${challenge.id}",
                            throwable
                        )
                    }
                }

                // 4c. Prefetch HARD Game: Follow Directions
                FollowDirectionsBank.getChallenges(story.id).forEach { challenge ->
                    runCatching {
                        repository.generateAudioForText(
                            id = challenge.id,
                            title = "Follow the Directions",
                            text = challenge.instructionText
                        ).getOrThrow()
                    }.onFailure { throwable ->
                        Log.e(
                            "AudioPrefetch",
                            "Failed Follow the Directions audio: ${challenge.id}",
                            throwable
                        )
                    }
                }

                // 4d. Prefetch HARD Game: Story Recall
                StoryRecallBank.getQuestions(story.id).forEach { q ->
                    runCatching {
                        repository.generateAudioForText(
                            id = q.id,
                            title = "Story Recall",
                            text = q.question
                        ).getOrThrow()
                    }.onFailure { throwable ->
                        Log.e(
                            "AudioPrefetch",
                            "Failed Story Recall audio: ${q.id}",
                            throwable
                        )
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
