package com.felipeserver.site.glyph.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    // Note functions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes ORDER BY timeStamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ")
    fun getNoteBySearch(query: String): Flow<NoteEntity?>

    @Query("SELECT id FROM notes ORDER BY id DESC LIMIT 1")
    suspend fun getLastNoteId(): Int?

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    // Tag functions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Update
    suspend fun updateTag(tag: TagEntity)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    // NoteTagCrossRef functions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTagToNote(crossRef: NoteTagCrossRef)

    @Query("DELETE FROM NoteTagCrossRef WHERE id = :noteId")
    suspend fun removeAllTagsFromNote(noteId: Int)

    @Transaction
    @Query("SELECT * FROM notes ORDER BY timeStamp DESC")
    fun getNotesWithTags(): Flow<List<NoteWithTags>>

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteWithTags(id: Int): Flow<NoteWithTags?>
}
