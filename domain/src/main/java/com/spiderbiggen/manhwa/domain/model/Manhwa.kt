package com.spiderbiggen.manhwa.domain.model

import java.net.URL

data class Manhwa(
    val source: String,
    val id: String,
    val title: String,
    val baseUrl: URL,
    val coverImage: URL?,
    val description: String?,
    val status: String,
)
