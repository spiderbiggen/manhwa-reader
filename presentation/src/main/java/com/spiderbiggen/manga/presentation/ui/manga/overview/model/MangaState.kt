package com.spiderbiggen.manga.presentation.ui.manga.overview.model

import com.spiderbiggen.manga.domain.model.Manga

data class MangaState(
    val manga: Manga,
    val isRead: Boolean,
    val isFavorite: Boolean,
)
