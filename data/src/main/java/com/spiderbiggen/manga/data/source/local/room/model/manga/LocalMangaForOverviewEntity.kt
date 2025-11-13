package com.spiderbiggen.manga.data.source.local.room.model.manga

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.spiderbiggen.manga.domain.model.id.ChapterId

data class LocalMangaForOverviewEntity(
    @Embedded
    val manga: LocalMangaEntity,
    @ColumnInfo("is_favorite")
    val isFavorite: Boolean,
    @ColumnInfo("is_read")
    val isRead: Boolean,
    @ColumnInfo("chapter_id")
    val lastChapterId: ChapterId?,
)
