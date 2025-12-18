package com.felipeserver.site.glyph.room
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

val db = Room.databaseBuilder(
    applicationContext,
    NoteDatabase::class.java,
    "notes.db"
).build()