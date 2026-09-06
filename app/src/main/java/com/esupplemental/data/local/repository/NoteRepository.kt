package com.esupplemental.data.local.repository

import com.esupplemental.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(userId: String): Flow<List<Note>>
    suspend fun getNoteById(noteId: String): Note?
    suspend fun saveNote(note: Note, userId: String)
    suspend fun deleteNote(noteId: String, userId: String)
}
