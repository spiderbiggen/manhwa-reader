package com.spiderbiggen.manga.data.source.local.model.chapter

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class LocalChapterForOverview(
    @Embedded
    val chapter: LocalChapterEntity,
    @ColumnInfo("is_read")
    val isRead: Boolean,
)
