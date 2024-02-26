package com.spiderbiggen.manhwa.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    tableName = "manga",
    indices = [
        Index("updated_at"),
    ]
)
data class LocalMangaEntity(
    @PrimaryKey
    val id: String,
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
