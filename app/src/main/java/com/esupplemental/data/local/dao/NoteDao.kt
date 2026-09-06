package com.esupplemental.data.local.dao

import androidx.room.*
import com.esupplemental.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotesForUser(userId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId AND userId = :userId")
    suspend fun deleteNoteById(noteId: String, userId: String)
}
