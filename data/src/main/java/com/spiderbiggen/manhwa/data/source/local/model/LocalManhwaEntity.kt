package com.spiderbiggen.manhwa.data.source.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity("manhwa")
data class LocalManhwaEntity(
    @PrimaryKey
    val id: String,
    val source: String,
    val title: String,
    val coverImage: String,
    val description: String?,
    val status: String,
    val updatedAt: LocalDate?,
)
