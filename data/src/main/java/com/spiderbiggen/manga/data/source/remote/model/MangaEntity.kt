package com.spiderbiggen.manga.data.source.remote.model

import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaEntity(
    val source: String,
    val id: MangaId,
    val title: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("dominant_color")
    val dominantColor: Int?,
    val description: String,
    val status: String,
    @SerialName("updated_at")
    val updatedAt: Instant,
)
