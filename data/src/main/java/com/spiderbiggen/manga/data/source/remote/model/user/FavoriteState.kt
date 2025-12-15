package com.spiderbiggen.manga.data.source.remote.model.user

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteState(
    @SerialName("is_favorite")
    val isFavorite: Boolean,
    @SerialName("updated_at")
    val updatedAt: Instant,
)
