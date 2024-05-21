package com.spiderbiggen.manhwa.presentation.ui.chapter

import com.spiderbiggen.manhwa.domain.model.Manga
import com.spiderbiggen.manhwa.presentation.ui.chapter.model.ChapterRowData

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
