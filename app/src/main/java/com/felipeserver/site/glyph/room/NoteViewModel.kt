package com.felipeserver.site.glyph.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteViewModel (application: Application) : AndroidViewModel(application) {

    private val dao = NoteDatabase.getDatabase(application).noteDao()


    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())

    val notes = _notes.asStateFlow()
}