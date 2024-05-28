package com.spiderbiggen.manga.data.source.remote.model

import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterEntity(
    val id: ChapterId,
    val number: Double,
    val title: String?,
    val date: LocalDate,
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("images")
    val images: Int,
)
