package com.esupplemental.data.model

import com.esupplemental.data.model.game.StoryEvent

data class StoryActivity(
    val mediaId: String,
    val reorderEvents: List<StoryEvent>,
    val multipleChoiceQuestions: List<MultipleChoiceQuestion>,
    val openEnded: List<OpenEndedQuestion>
)