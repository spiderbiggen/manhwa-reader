package com.spiderbiggen.manga.presentation.ui.manga.model

import androidx.compose.runtime.Immutable

@Immutable
data class MangaViewData(
    val id: String,
    val source: String,
    val title: String,
    val status: String,
    val coverImage: String,
    val updatedAt: String?,
    val isFavorite: Boolean,
    val readAll: Boolean,
)
