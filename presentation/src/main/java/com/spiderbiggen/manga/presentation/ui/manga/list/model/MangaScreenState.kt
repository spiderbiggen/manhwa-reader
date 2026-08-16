package com.spiderbiggen.manga.presentation.ui.manga.list.model

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manga.presentation.ui.manga.list.MangaFilter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

@Immutable
data class MangaScreenData(
    val activeFilters: ImmutableSet<MangaFilter> = persistentSetOf(),
    val state: MangaScreenState = MangaScreenState.Loading,
)

@Immutable
sealed interface MangaScreenState {
    @Immutable data object Loading : MangaScreenState

    @Immutable data class Ready(val manga: ImmutableList<MangaViewData>) : MangaScreenState

    @Immutable data class Error(val message: String) : MangaScreenState
}
