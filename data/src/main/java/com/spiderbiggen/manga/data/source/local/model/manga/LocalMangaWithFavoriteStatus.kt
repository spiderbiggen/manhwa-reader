package com.spiderbiggen.manga.data.source.local.model.manga

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class LocalMangaWithFavoriteStatus(
    @Embedded
    val manga: LocalMangaEntity,
    @ColumnInfo("is_favorite")
    val isFavorite: Boolean,
)
