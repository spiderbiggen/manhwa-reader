package com.spiderbiggen.manga.data.source.local.model.manga

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant

@Entity(
    tableName = "manga",
    indices = [
        Index("updated_at"),
    ],
)
data class LocalMangaEntity(
    @PrimaryKey
    val id: MangaId,
    val source: String,
    val title: String,
    @ColumnInfo("cover")
    val cover: String,
    @ColumnInfo("dominant_color", defaultValue = "NULL")
    val dominantColor: Int?,
    val description: String,
    val status: String,
    @ColumnInfo("updated_at")
    val updatedAt: Instant,
)
