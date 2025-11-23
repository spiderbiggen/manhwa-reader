package com.spiderbiggen.manga.data.source.remote.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenBody(
    @SerialName("refresh_token")
    val refreshToken: String,
)
