package com.spiderbiggen.manhwa.data.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManhwaEntity(
    val source: String,
    val id: String,
    val title: String,
    @SerialName("base_url")
    val baseUrl: String,
    @SerialName("cover_image")
    val coverImage: String,
    val description: String? = null,
    val status: String,
)
