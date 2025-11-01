package com.spiderbiggen.manga.data.source.local.model.manga

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(tableName = "manga_favorite_status")
data class MangaFavoriteStatusEntity(
    @PrimaryKey
    val id: MangaId,
    @ColumnInfo("is_favorite")
    val isFavorite: Boolean = true,
    @ColumnInfo("updated_at")
    val updatedAt: Instant = Clock.System.now(),
)
