package com.spiderbiggen.manga.domain.model.manga

import com.spiderbiggen.manga.domain.model.id.ChapterId

data class MangaForOverview(
    val manga: Manga,
    val isFavorite: Boolean,
    val isRead: Boolean,
    val lastChapterId: ChapterId?,
)
