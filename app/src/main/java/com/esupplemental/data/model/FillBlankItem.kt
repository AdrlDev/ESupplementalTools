package com.esupplemental.data.model

data class FillBlankItem(
    val id: String,
    val sentence: String,           // e.g. "The lion __ the mouse."
    val answer: String
)