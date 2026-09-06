package com.esupplemental.data.model.game

data class DisappearingTextQuestion(
    val id: String,
    val sentence: String,
    val sourceTitle: String = ""
)

data class TwoTruthsLieQuestion(
    val id: String,
    val sourceTitle: String,
    val audioText: String,
    val statements: List<Statement>
)

data class Statement(
    val text: String,
    val isTrue: Boolean
)

data class MinimalPairQuestion(
    val id: String,
    val wordA: String,
    val wordB: String,
    val correctWord: String
)
