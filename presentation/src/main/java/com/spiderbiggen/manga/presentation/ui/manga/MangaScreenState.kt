package com.spiderbiggen.manga.presentation.ui.manga

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaViewData
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface MangaScreenState {
    @Immutable
    data object Loading : MangaScreenState

    @Immutable
    data class Ready(
        val manga: ImmutableList<MangaViewData>,
        val favoritesOnly: Boolean,
        val unreadOnly: Boolean,
    ) : MangaScreenState

    @Immutable
    data class Error(val message: String) : MangaScreenState
}
