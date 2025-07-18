package com.spiderbiggen.manga.data.source.remote.model

import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant
import kotlinx.serialization.Contextual
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
    @Contextual
    @SerialName("updated_at")
    val updatedAt: Instant,
)
