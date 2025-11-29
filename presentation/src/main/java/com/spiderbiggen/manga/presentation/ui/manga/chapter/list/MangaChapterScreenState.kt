package com.spiderbiggen.manga.presentation.ui.manga.chapter.list

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.model.ChapterRowData
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface MangaChapterScreenState {
    @Immutable
    data object Loading : MangaChapterScreenState

    @Immutable
    data class Ready(val title: String, val isFavorite: Boolean, val chapters: ImmutableList<ChapterRowData>) :
        MangaChapterScreenState

    @Immutable
    data class Error(val message: String) : MangaChapterScreenState
}
