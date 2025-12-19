package com.felipeserver.site.glyph.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = NoteDatabase.getDatabase(application).noteDao()

    val notes: StateFlow<List<NoteEntity>> = dao.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        insertInitialNotesIfEmpty()
    }

    private fun insertInitialNotesIfEmpty() {
        viewModelScope.launch {
            // Use .first() to get the first emitted list from the Flow
            if (notes.first().isEmpty()) {
                // Insert 15 notes to make the list scrollable
                for (i in 1..15) {
                    val note = NoteEntity(
                        title = "Note Title $i",
                        content = "This is the content for note #$i",
                        timeStamp = System.currentTimeMillis()
                    )
                    dao.insertNote(note)
                }
            }
        }
    }
}