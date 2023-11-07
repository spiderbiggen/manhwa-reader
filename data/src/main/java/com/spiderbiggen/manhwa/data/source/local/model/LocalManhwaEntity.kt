package com.spiderbiggen.manhwa.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity("manhwa")
data class LocalManhwaEntity(
    @PrimaryKey
    val id: String,
    val source: String,
    val title: String,
    @ColumnInfo("cover")
    val cover: String,
    val description: String,
    val status: String,
    @ColumnInfo("updated_at")
    val updatedAt: LocalDate,
)
