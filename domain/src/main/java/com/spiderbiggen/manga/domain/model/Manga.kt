package com.spiderbiggen.manga.domain.model

import com.spiderbiggen.manga.domain.model.id.MangaId
import java.net.URL
import kotlin.time.Instant

data class Manga(
    val source: String,
    val id: MangaId,
    val title: String,
    val coverImage: URL,
    val dominantColor: Int?,
    val description: String?,
    val status: String,
    val updatedAt: Instant,
)
