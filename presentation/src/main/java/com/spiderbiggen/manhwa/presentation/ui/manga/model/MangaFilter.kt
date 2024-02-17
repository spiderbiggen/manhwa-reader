package com.spiderbiggen.manhwa.presentation.ui.manga.model

sealed interface MangaFilter {

    operator fun invoke(mangaList: List<MangaViewData>): List<MangaViewData> = filter(mangaList)
    fun filter(mangaList: List<MangaViewData>): List<MangaViewData>

    data object Unread : MangaFilter {
        override fun filter(mangaList: List<MangaViewData>) =
            mangaList.filterNot(MangaViewData::readAll)

    }

    data object Favorites : MangaFilter {
        override fun filter(mangaList: List<MangaViewData>) =
            mangaList.filter(MangaViewData::isFavorite)
    }

    data class TitleSearch(val matcher: String) : MangaFilter {
        override fun filter(mangaList: List<MangaViewData>) =
            mangaList.filter { it.title.contains(matcher, ignoreCase = true) }

    }
}