package com.spiderbiggen.manga.data.source.local.room.model.manga

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant

@Entity(
    tableName = "manga",
    indices = [Index("updated_at")],
)
data class LocalMangaEntity(
    @PrimaryKey val id: MangaId,
    val source: String,
    val title: String,
    @ColumnInfo("cover") val cover: String,
    @ColumnInfo("dominant_color", defaultValue = "NULL") val dominantColor: Int?,
    val description: String,
    val status: String,
    @ColumnInfo("updated_at") val updatedAt: Instant,
)
