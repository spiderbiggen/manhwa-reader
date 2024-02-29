package com.spiderbiggen.manhwa.domain.model

import java.net.URL
import kotlinx.datetime.Instant

data class Manga(
    val source: String,
    val id: String,
    val title: String,
    val coverImage: URL,
    val dominantColor: Int?,
    val description: String?,
    val status: String,
    val updatedAt: Instant,
)
