package com.spiderbiggen.manhwa.data.source.remote.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterEntity(
    val id: String,
    val number: Int,
    val decimal: Int? = null,
    val title: String? = null,
    val url: String,
    val date: LocalDate? = null,
    @SerialName("indexed_images")
    val imageChunks: Int? = null
)