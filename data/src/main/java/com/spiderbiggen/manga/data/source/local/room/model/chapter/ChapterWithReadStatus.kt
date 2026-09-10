package com.spiderbiggen.manga.data.source.local.room.model.chapter

import androidx.room3.ColumnInfo
import androidx.room3.Embedded

data class ChapterWithReadStatus(
    @Embedded val chapter: LocalChapterEntity,
    @ColumnInfo("is_read") val isRead: Boolean,
)
