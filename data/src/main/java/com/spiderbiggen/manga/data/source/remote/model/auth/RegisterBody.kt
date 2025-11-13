package com.spiderbiggen.manga.data.source.remote.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterBody(
    val username: String,
    val email: String?,
    val password: String,
)
