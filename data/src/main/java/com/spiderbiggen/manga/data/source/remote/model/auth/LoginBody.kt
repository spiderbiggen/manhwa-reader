package com.spiderbiggen.manga.data.source.remote.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginBody(
    @SerialName("username_or_email")
    val usernameOrEmail: String,
    val password: String,
)
