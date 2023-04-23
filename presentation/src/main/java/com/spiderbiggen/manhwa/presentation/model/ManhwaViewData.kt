package com.spiderbiggen.manhwa.presentation.model

data class ManhwaViewData(
    val id: String,
    val source: String,
    val title: String,
    val status: String,
    val coverImage: String,
    val updatedAt: String?,
    val isFavorite: Boolean,
)
