package com.esupplemental.data.local.entity

import androidx.room.*

@Entity(tableName = "story_activities")
data class StoryActivityEntity(
    @PrimaryKey val mediaId: String,
    val reorderEventsJson: String,    // JSON array of StoryEvent
    val multipleChoiceQuestions: String,
    val openEndedJson: String         // JSON array of OpenEndedQuestion
)