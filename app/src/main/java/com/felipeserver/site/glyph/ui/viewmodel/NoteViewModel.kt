package com.felipeserver.site.glyph.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.felipeserver.site.glyph.data.local.NoteDatabase
import com.felipeserver.site.glyph.data.local.NoteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

// Data class to represent the UI state for the NoteView
data class NoteUiState(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val isNewNote: Boolean = true
)

class NoteViewModel(application: Application) : AndroidViewModel(application) {


    private val dao = NoteDatabase.getDatabase(application).noteDao()


    private val _uiState = MutableStateFlow(NoteUiState())


    val uiState = _uiState.asStateFlow()

    val notes: StateFlow<List<NoteEntity>> = dao.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getNoteById(id: Int) {
        viewModelScope.launch {
            dao.getNoteById(id).collect { note ->
                if (note != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            isNewNote = false
                        )
                    }
                }
            }
        }
    }

    fun getNoteBySearch(query: String) {
        viewModelScope.launch {
            dao.getNoteBySearch(query).collect { note ->
                if (note != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            isNewNote = false
                        )
                    }
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    suspend fun getLastNoteId(): Int? {
        return dao.getLastNoteId()
    }

    fun saveNote() {
        viewModelScope.launch {
            val currentNoteState = _uiState.value
            val noteEntity = NoteEntity(
                id = if (currentNoteState.isNewNote) 0 else currentNoteState.id, // Room handles ID for new entries
                title = currentNoteState.title,
                content = currentNoteState.content,
                timeStamp = Instant.now()
            )
            if (currentNoteState.isNewNote) {
                dao.insertNote(noteEntity)
            } else {
                dao.updateNote(noteEntity)
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            dao.deleteNote(note)
        }
    }
}