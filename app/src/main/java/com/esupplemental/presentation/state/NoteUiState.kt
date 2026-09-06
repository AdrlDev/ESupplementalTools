package com.esupplemental.presentation.state

import com.esupplemental.data.model.Note

/**
 * UI State for the Note screen.
 * Uses plain Strings instead of TextFieldValue to maintain architecture purity.
 */
data class NoteUiState(
    val noteId: String = "",
    val title: String = "",
    val mainIdea: String = "",
    val keyDetails: String = "",
    val summary: String = "",
    val keywords: List<String> = emptyList(),
    val newKeyword: String = "",
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isSaved: Boolean = false,
    val allNotes: List<Note> = emptyList()
)