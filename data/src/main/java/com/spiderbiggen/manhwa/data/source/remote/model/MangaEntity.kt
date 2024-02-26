package com.spiderbiggen.manhwa.data.source.remote.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaEntity(
    val source: String,
    val id: String,
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
