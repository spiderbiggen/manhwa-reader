package com.spiderbiggen.manhwa.data.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterWithImageChunksEntity(
    val id: String,
    val number: Int,
    val decimal: Int? = null,
    val title: String? = null,
    val url: String,
    @SerialName("image_chunks")
    val imageChunks: Int
)