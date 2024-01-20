package com.spiderbiggen.manhwa.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "chapter",
    foreignKeys = [
        ForeignKey(LocalMangaEntity::class, ["id"], ["manga_id"])
    ],
    indices = [
        Index("manga_id"),
        Index("number", "decimal"),
    ]
)
data class LocalChapterEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo("manga_id")
    val mangaId: String,
    val number: Int,
    @ColumnInfo(defaultValue = "0")
    val decimal: Int,
    val title: String? = null,
    val date: LocalDate,
    @ColumnInfo("updated_at")
    val updatedAt: Instant? = null,
    @ColumnInfo("image_chunks", defaultValue = "0")
    val imageChunks: Int
)