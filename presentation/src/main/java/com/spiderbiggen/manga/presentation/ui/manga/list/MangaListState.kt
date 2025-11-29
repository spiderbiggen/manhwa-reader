package com.spiderbiggen.manga.presentation.ui.manga.list

import com.spiderbiggen.manga.domain.model.manga.Manga

data class MangaListState(val manga: Manga, val isRead: Boolean, val isFavorite: Boolean)
