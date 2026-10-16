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
        val allPoems = repository.getMediaList(MediaType.POEM).first()
        val allPodcasts = repository.getMediaList(MediaType.PODCAST).first()

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

        // 8. Prefetch full poem audio and every poem question in the background.
        // Poem audio is generated from the poem body only; quiz content is never
        // included in the narration. Force refresh once for this work version so
        // stale server URLs from earlier installs are replaced.
        allPoems.forEach { poem ->
            try {
                val markdown = StoryLoader.load(application, poem.transcript)
                val poemBody = com.esupplemental.domain.utils.PoemActivityFactory.extractTranscript(markdown)
                repository.generateAudioForText(
                    id = poem.id,
                    title = poem.title,
                    text = poemBody,
                    forceRefresh = true
                ).getOrThrow()

                repository.getStoryActivity(poem.id)?.multipleChoiceQuestions?.forEach { question ->
                    runCatching {
                        repository.generateAudioForText(
                            id = question.id,
                            title = "Poem question",
                            text = question.question,
                            forceRefresh = true
                        ).getOrThrow()
                    }.onFailure { throwable ->
                        Log.e("AudioPrefetch", "Failed poem question audio: ${question.id}", throwable)
                    }
                }
            } catch (e: Exception) {
                Log.e("AudioPrefetch", "Failed poem audio: ${poem.id}", e)
            }
        }

        // 9. Prefetch podcast challenge question audio. Podcast videos remain
        // remote video sources; only their challenge prompts use generated audio.
        allPodcasts.forEach { podcast ->
            try {
                val activity = repository.getSongActivity(podcast.id) ?: return@forEach
                activity.fillBlanks.forEach { item ->
                    runCatching {
                        repository.generateAudioForText(
                            id = item.id,
                            title = "Podcast question",
                            text = item.sentence.replace("___", "blank"),
                            forceRefresh = true
                        ).getOrThrow()
                    }.onFailure { throwable ->
                        Log.e("AudioPrefetch", "Failed podcast question audio: ${item.id}", throwable)
                    }
                }
                runCatching {
                    repository.generateAudioForText(
                        id = activity.messageQuestion.id,
                        title = "Podcast question",
                        text = activity.messageQuestion.question,
                        forceRefresh = true
                    ).getOrThrow()
                }.onFailure { throwable ->
                    Log.e("AudioPrefetch", "Failed podcast message audio: ${activity.messageQuestion.id}", throwable)
                }
            } catch (e: Exception) {
                Log.e("AudioPrefetch", "Failed podcast audio: ${podcast.id}", e)
            }
        }
    }
}
