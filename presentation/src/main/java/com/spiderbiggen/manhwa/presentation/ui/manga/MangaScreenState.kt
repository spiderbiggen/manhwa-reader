package com.spiderbiggen.manhwa.presentation.ui.manga

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manhwa.presentation.ui.manga.model.MangaViewData

@Immutable
sealed interface MangaScreenState {
    @Immutable
    data object Loading : MangaScreenState

    @Immutable
    data class Ready(
        val manga: List<MangaViewData>,
        val favoritesOnly: Boolean,
        val unreadOnly: Boolean,
    ) : MangaScreenState

    @Immutable
    data class Error(val message: String) : MangaScreenState
}
