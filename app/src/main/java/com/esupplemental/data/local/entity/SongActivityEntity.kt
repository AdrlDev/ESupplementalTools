package com.esupplemental.data.local.entity

import androidx.room.*

@Entity(tableName = "song_activities")
data class SongActivityEntity(
    @PrimaryKey val mediaId: String,
    val fillBlanksJson: String,       // JSON array of FillBlankItem
    val messageQuestionJson: String   // JSON of MultipleChoiceItem
)