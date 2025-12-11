package com.spiderbiggen.manga.domain.model.manga

import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant

data class Manga(
    val source: String,
    val id: MangaId,
    val title: String,
    val coverImage: String,
    val dominantColor: Int?,
    val description: String?,
    val status: String,
    val updatedAt: Instant,
)
