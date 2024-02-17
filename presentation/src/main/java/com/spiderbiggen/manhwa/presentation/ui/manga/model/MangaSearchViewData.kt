package com.spiderbiggen.manhwa.presentation.ui.manga.model

import androidx.compose.runtime.Immutable

@Immutable
data class MangaSearchViewData(
    val query: String = "",
    val onlyFavorites: Boolean = false,
    val onlyRead: Boolean = false,
)