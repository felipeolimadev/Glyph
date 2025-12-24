package com.felipeserver.site.glyph.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.felipeserver.site.glyph.data.local.NoteDatabase
import com.felipeserver.site.glyph.data.local.NoteEntity
import com.felipeserver.site.glyph.data.local.NoteTagCrossRef
import com.felipeserver.site.glyph.data.local.NoteWithTags
import com.felipeserver.site.glyph.data.local.TagEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

data class NoteUiState(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val tags: List<TagEntity> = emptyList(),
    val isNewNote: Boolean = true
)

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = NoteDatabase.getDatabase(application).noteDao()

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    val notesWithTags: StateFlow<List<NoteWithTags>> = dao.getNotesWithTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allTags: StateFlow<List<TagEntity>> = dao.getAllTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getNoteWithTags(id: Int) {
        viewModelScope.launch {
            dao.getNoteWithTags(id).collect { noteWithTags ->
                if (noteWithTags != null) {
                    _uiState.update {
                        it.copy(
                            id = noteWithTags.note.id,
                            title = noteWithTags.note.title,
                            content = noteWithTags.note.content,
                            tags = noteWithTags.tags,
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

    fun saveNote() {
        viewModelScope.launch {
            val currentNoteState = _uiState.value
            val noteEntity = NoteEntity(
                id = if (currentNoteState.isNewNote) 0 else currentNoteState.id,
                title = currentNoteState.title,
                content = currentNoteState.content,
                timeStamp = Instant.now()
            )

            val noteId = if (currentNoteState.isNewNote) {
                dao.insertNote(noteEntity)
            } else {
                dao.updateNote(noteEntity)
                noteEntity.id.toLong()
            }

            // Update tags for the note
            dao.removeAllTagsFromNote(noteId.toInt())
            currentNoteState.tags.forEach { tag ->
                dao.addTagToNote(NoteTagCrossRef(noteId.toInt(), tag.tagId))
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            dao.deleteNote(note)
        }
    }

    // --- Tag Management ---

    fun createTag(tagName: String) {
        viewModelScope.launch {
            val newTag = TagEntity(name = tagName)
            dao.insertTag(newTag)
        }
    }

    fun addTagToCurrentNote(tag: TagEntity) {
        _uiState.update { currentState ->
            if (currentState.tags.any { it.tagId == tag.tagId }) {
                currentState // Tag already exists, do nothing
            } else {
                currentState.copy(tags = currentState.tags + tag)
            }
        }
    }

    fun removeTagFromCurrentNote(tag: TagEntity) {
        _uiState.update { currentState ->
            currentState.copy(tags = currentState.tags.filter { it.tagId != tag.tagId })
        }
    }

    fun updateTag(tag: TagEntity) {
        viewModelScope.launch {
            dao.updateTag(tag)
        }
    }

    fun deleteTag(tag: TagEntity) {
        viewModelScope.launch {
            dao.deleteTag(tag)
        }
    }
     suspend fun getLastNoteId(): Int? {
        return dao.getLastNoteId()
    }
}
