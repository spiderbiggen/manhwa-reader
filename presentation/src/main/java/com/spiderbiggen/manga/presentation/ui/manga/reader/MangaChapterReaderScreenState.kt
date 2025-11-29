package com.spiderbiggen.manga.presentation.ui.manga.reader

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface MangaChapterReaderScreenState {
    @Immutable
    data object Loading : MangaChapterReaderScreenState

    @Immutable
    data class Ready(
        val title: String,
        val isFavorite: Boolean,
        val isRead: Boolean,
        val surrounding: SurroundingChapters,
        val images: ImmutableList<String>,
    ) : MangaChapterReaderScreenState

    @Immutable
    data class Error(val errorMessage: String) : MangaChapterReaderScreenState

    fun ifReady(): Ready? = this as? Ready
}
