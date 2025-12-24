package com.felipeserver.site.glyph.data.local

import androidx.room.Entity

@Entity(primaryKeys = ["id", "tagId"])
data class NoteTagCrossRef(
    val id: Int,
    val tagId: Int
)
