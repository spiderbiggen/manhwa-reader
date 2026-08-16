package com.spiderbiggen.manga.presentation.ui.manga.list

import com.spiderbiggen.manga.domain.model.id.MangaId

sealed interface MangaListAction {
    data object Refresh : MangaListAction

    data class SetFilter(val filter: MangaFilter, val enabled: Boolean) : MangaListAction

    data object ClearFilters : MangaListAction

    data class FavoriteClicked(val id: MangaId) : MangaListAction
}
