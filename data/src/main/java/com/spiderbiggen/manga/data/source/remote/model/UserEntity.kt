package com.spiderbiggen.manga.data.source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val username: String,
    val email: String? = null,
    val avatar: String? = null,
)
