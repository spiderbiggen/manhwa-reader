package com.spiderbiggen.manga.domain.model.auth

import kotlin.time.Instant

data class User(
    val id: String,
    val username: String,
    val avatarUrl: String,
    val email: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
)
