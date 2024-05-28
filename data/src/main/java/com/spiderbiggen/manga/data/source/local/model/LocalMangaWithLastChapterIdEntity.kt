package com.spiderbiggen.manga.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.spiderbiggen.manga.domain.model.id.ChapterId

data class LocalMangaWithLastChapterIdEntity(
    @Embedded
    val manga: LocalMangaEntity,
    @ColumnInfo("chapter_id")
    val lastChapterId: ChapterId?,
)
