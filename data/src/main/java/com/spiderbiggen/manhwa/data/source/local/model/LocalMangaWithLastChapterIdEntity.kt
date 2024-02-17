package com.spiderbiggen.manhwa.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class LocalMangaWithLastChapterIdEntity(
    @Embedded
    val manga: LocalMangaEntity,

    @ColumnInfo("chapter_id")
    val lastChapterId: String?,
)