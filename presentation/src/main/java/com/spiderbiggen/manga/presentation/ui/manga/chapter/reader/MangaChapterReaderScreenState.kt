package com.spiderbiggen.manga.presentation.ui.manga.chapter.reader

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface MangaChapterReaderScreenState {

    val title: String?
        get() = null

    @Immutable
    data class Loading(override val title: String?) : MangaChapterReaderScreenState

    @Immutable
    data class Ready(
        override val title: String,
        val isFavorite: Boolean,
        val isRead: Boolean,
        val surrounding: SurroundingChapters,
        val images: ImmutableList<String>,
    ) : MangaChapterReaderScreenState

    @Immutable
    data class Error(val errorMessage: String) : MangaChapterReaderScreenState
}
