package com.spiderbiggen.manga.data.source.remote.model.auth

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenEntity(
    val token: String,
    @SerialName("expires_at")
    val expiresAt: Instant,
)
