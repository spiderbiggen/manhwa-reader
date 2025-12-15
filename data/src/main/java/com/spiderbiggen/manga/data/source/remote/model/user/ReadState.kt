package com.spiderbiggen.manga.data.source.remote.model.user

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadState(
    @SerialName("is_read")
    val isRead: Boolean,
    @SerialName("updated_at")
    val updatedAt: Instant,
)
