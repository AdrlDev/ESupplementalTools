package com.esupplemental.data.model

data class SongActivity(
    val mediaId: String,
    val fillBlanks: List<FillBlankItem>,
    val messageQuestion: MultipleChoiceItem
)