package com.esupplemental.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esupplemental.data.local.repository.NoteRepository
import com.esupplemental.data.model.Note
import com.esupplemental.domain.utils.UserPreferences
import com.esupplemental.presentation.state.NoteUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    init {
        observeNotes()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeNotes() {
        viewModelScope.launch {
            userPreferences.userId.flatMapLatest { id ->
                if (id != null) {
                    noteRepository.getNotes(id)
                } else {
                    flowOf(emptyList())
                }
            }.collect { notes ->
                _uiState.update { it.copy(allNotes = notes) }
            }
        }
    }

    /**
     * Loads a note by ID or prepares a new note if noteId is "new".
     * Prevents duplicate initialization by checking if the note is already loaded.
     */
    fun loadNote(noteId: String) {
        if (noteId == "new") {
            // Only initialize a new note if the current state is not already a "new" note
            if (_uiState.value.noteId.isEmpty() || _uiState.value.isSaved) {
                newNote()
            }
            return
        }

        // Avoid reloading the same note
        if (_uiState.value.noteId == noteId) return

        viewModelScope.launch {
            val note = noteRepository.getNoteById(noteId) ?: return@launch
            _uiState.update { it.copy(
                noteId = note.id,
                title = note.title,
                mainIdea = note.mainIdea,
                keyDetails = note.keyDetails,
                summary = note.summary,
                keywords = note.keywords,
                isSaved = true
            )}
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, isSaved = false) }
    }

    fun onMainIdeaChange(value: String) {
        _uiState.update { it.copy(mainIdea = value, isSaved = false) }
    }

    fun onKeyDetailsChange(value: String) {
        _uiState.update { it.copy(keyDetails = value, isSaved = false) }
    }

    fun onSummaryChange(value: String) {
        _uiState.update { it.copy(summary = value, isSaved = false) }
    }

    fun toggleBold() {
        _uiState.update { it.copy(isBold = !it.isBold) }
    }

    fun toggleItalic() {
        _uiState.update { it.copy(isItalic = !it.isItalic) }
    }

    fun toggleUnderline() {
        _uiState.update { it.copy(isUnderline = !it.isUnderline) }
    }

    fun onNewKeywordChange(value: String) {
        _uiState.update { it.copy(newKeyword = value) }
    }

    fun addKeyword() {
        val kw = _uiState.value.newKeyword.trim()
        if (kw.isNotEmpty() && !_uiState.value.keywords.contains(kw)) {
            _uiState.update { it.copy(
                keywords = it.keywords + kw,
                newKeyword = ""
            )}
        }
    }

    fun removeKeyword(keyword: String) {
        _uiState.update { it.copy(
            keywords = it.keywords - keyword
        )}
    }

    fun saveNote() {
        viewModelScope.launch {
            val state = _uiState.value
            val userId = userPreferences.userId.first() ?: return@launch
            
            // Generate ID if missing (should not happen with newNote() call)
            val finalId = state.noteId.ifEmpty { UUID.randomUUID().toString() }
            
            val note = Note(
                id = finalId,
                title = state.title,
                mainIdea = state.mainIdea,
                keyDetails = state.keyDetails,
                summary = state.summary,
                keywords = state.keywords
            )
            noteRepository.saveNote(note, userId)
            _uiState.update { it.copy(noteId = finalId, isSaved = true) }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            val userId = userPreferences.userId.first() ?: return@launch
            noteRepository.deleteNote(noteId, userId)
        }
    }

    fun newNote() {
        _uiState.update {
            NoteUiState(
                noteId = UUID.randomUUID().toString(),
                allNotes = it.allNotes
            )
        }
    }
}
