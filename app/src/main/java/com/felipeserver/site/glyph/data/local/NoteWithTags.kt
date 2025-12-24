package com.felipeserver.site.glyph.data.local

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NoteWithTags(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tagId",
        associateBy = Junction(NoteTagCrossRef::class)
    )
    val tags: List<TagEntity>
)
