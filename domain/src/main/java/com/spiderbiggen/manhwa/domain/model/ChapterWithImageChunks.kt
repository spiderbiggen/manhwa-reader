package com.spiderbiggen.manhwa.domain.model

import java.net.URL

data class ChapterWithImageChunks(
    val id: String,
    val number: Int,
    val decimal: Int?,
    val title: String?,
    val url: URL,
    val imageChunks: List<URL>,
)
