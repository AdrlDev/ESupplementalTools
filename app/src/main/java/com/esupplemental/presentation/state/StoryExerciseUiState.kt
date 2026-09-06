package com.esupplemental.presentation.state

import com.esupplemental.data.model.StoryActivity
import com.esupplemental.data.model.game.StoryEvent

data class StoryExerciseUiState(
    val mediaId: String = "",
    val activity: StoryActivity? = null,
    val orderedEvents: List<StoryEvent> = emptyList(),   // user's current ordering
    val openEndedAnswers: Map<String, String> = emptyMap(),
    val mcAnswers: Map<String, Int> = emptyMap(),   // questionId -> selected choice index
    val mcScore: Int = 0,
    val isSubmitted: Boolean = false,
    val isSubmitting: Boolean = false,
    val reorderScore: Int = 0,
    val totalEvents: Int = 0,
    val combinedScore: Int = 0,
    val combinedTotal: Int = 0,
    val userAnswersList: List<String> = emptyList(),
    val correctAnswersList: List<String> = emptyList(),
    val resultId: Long? = null,
    
    // Maps ID (question/event) to loading status
    val loadingAudioIds: Set<String> = emptySet(),
    val audioError: String? = null,
    val submissionError: String? = null
)
