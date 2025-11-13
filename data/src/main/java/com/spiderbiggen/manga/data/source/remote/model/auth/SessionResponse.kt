package com.spiderbiggen.manga.data.source.remote.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    @SerialName("access_token")
    val accessToken: TokenEntity,
    @SerialName("refresh_token")
    val refreshToken: TokenEntity,
)
