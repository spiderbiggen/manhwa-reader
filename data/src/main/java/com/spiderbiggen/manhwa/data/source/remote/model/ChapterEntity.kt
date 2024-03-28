package com.spiderbiggen.manhwa.data.source.remote.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterEntity(
    val id: String,
    val number: Double,
    val title: String?,
    val date: LocalDate,
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("images")
    val images: Int,
)
