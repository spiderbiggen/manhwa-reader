package com.spiderbiggen.manga.presentation.ui.manga.list

import com.spiderbiggen.manga.domain.model.manga.MangaForOverview

enum class MangaFilter(val persistedKey: String) {
    Unread("unread") {
        override fun matches(manga: MangaForOverview) = !manga.isRead
    },
    Favorites("favorites") {
        override fun matches(manga: MangaForOverview) = manga.isFavorite
    };

    abstract fun matches(manga: MangaForOverview): Boolean
}
