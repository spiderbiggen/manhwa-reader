package com.spiderbiggen.manhwa.presentation.ui.manga

import com.spiderbiggen.manhwa.presentation.model.MangaViewData

sealed interface MangaScreenState {
    data object Loading : MangaScreenState
    data class Ready(val manga: List<MangaViewData>) : MangaScreenState
    data class Error(val message: String) : MangaScreenState
}