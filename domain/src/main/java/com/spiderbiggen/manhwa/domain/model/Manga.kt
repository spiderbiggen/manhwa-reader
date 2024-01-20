package com.spiderbiggen.manhwa.domain.model

import kotlinx.datetime.Instant
import java.net.URL

data class Manga(
    val source: String,
    val id: String,
    val title: String,
    val coverImage: URL,
    val description: String?,
    val status: String,
    val updatedAt: Instant
)
