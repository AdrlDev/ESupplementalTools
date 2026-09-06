package com.esupplemental.data.local.repository.impl

import com.esupplemental.data.local.dao.NoteDao
import com.esupplemental.data.local.repository.NoteRepository
import com.esupplemental.data.mapper.DataMapper.toDomainModel
import com.esupplemental.data.mapper.DataMapper.toEntity
import com.esupplemental.data.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(private val noteDao: NoteDao) : NoteRepository {
    override fun getNotes(userId: String): Flow<List<Note>> =
        noteDao.getNotesForUser(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }

    override suspend fun getNoteById(noteId: String): Note? {
        return noteDao.getNoteById(noteId)?.toDomainModel()
    }

    override suspend fun saveNote(note: Note, userId: String) {
        noteDao.insertNote(note.toEntity(userId))
    }

    override suspend fun deleteNote(noteId: String, userId: String) {
        noteDao.deleteNoteById(noteId, userId)
    }
}
