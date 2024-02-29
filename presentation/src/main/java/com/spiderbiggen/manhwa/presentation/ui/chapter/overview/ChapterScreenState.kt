package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import com.spiderbiggen.manhwa.domain.model.Manga

sealed interface ChapterScreenState {
    data object Loading : ChapterScreenState
    data class Ready(
        val manga: Manga,
        val isFavorite: Boolean,
        val chapters: List<ChapterRowData>,
    ) : ChapterScreenState

    data class Error(val message: String) : ChapterScreenState

    fun ifReady(): Ready? = this as? Ready
}
