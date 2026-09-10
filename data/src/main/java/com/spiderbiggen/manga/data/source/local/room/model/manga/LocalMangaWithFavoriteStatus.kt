package com.spiderbiggen.manga.data.source.local.room.model.manga

import androidx.room3.ColumnInfo
import androidx.room3.Embedded

data class LocalMangaWithFavoriteStatus(
    @Embedded val manga: LocalMangaEntity,
    @ColumnInfo("is_favorite") val isFavorite: Boolean,
)
