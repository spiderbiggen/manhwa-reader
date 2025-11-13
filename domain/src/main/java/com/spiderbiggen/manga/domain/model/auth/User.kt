package com.spiderbiggen.manga.domain.model.auth

data class User(
    val username: String,
    val email: String? = null,
    val avatar: String? = null,
)
