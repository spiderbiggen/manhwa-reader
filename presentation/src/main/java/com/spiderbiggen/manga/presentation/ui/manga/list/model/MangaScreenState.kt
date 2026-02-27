package com.spiderbiggen.manga.presentation.ui.manga.list.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class MangaScreenData(
    val filterFavorites: Boolean = false,
    val filterUnread: Boolean = false,
    val state: MangaScreenState = MangaScreenState.Loading,
)

@Immutable
sealed interface MangaScreenState {
    @Immutable
    data object Loading : MangaScreenState

    @Immutable
    data class Ready(val manga: ImmutableList<MangaViewData>) : MangaScreenState

    @Immutable
    data class Error(val message: String) : MangaScreenState
}
