package com.spiderbiggen.manhwa.data.source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ChapterEntity(
    val id: String,
    val number: Int,
    val decimal: Int? = null,
    val title: String? = null,
    val url: String,
)