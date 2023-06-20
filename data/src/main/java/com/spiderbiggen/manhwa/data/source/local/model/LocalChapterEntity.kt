package com.spiderbiggen.manhwa.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "chapter",
    foreignKeys = [
        ForeignKey(LocalManhwaEntity::class, ["id"], ["manhwa_id"])
    ],
    indices = [
        Index("manhwa_id")
    ]
)
data class LocalChapterEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo("manhwa_id")
    val manhwaId: String,
    val number: Int,
    val decimal: Int? = null,
    val title: String? = null,
    val url: String,
    val date: LocalDate? = null,
    @ColumnInfo("image_chunks")
    val imageChunks: Int? = null
)