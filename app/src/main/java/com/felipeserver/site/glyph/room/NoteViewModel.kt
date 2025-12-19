package com.felipeserver.site.glyph.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = NoteDatabase.getDatabase(application).noteDao()


    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())

    val notes = _notes.asStateFlow()

    init {
        insertInitialNote()
    }

    private fun insertInitialNote() {
        viewModelScope.launch {
            val currentNotes = dao.getAllNotes()

            if (currentNotes.isEmpty()) {
                val initialNote = NoteEntity(
                    title = "Welcome",
                    content = "This is your first note",
                    timeStamp = System.currentTimeMillis()
                )
                dao.insertNote(initialNote)

            }
            _notes.value = dao.getAllNotes()
        }
    }
}