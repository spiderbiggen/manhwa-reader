package com.spiderbiggen.manga.presentation.ui.chapter.overview

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.spiderbiggen.manga.presentation.ui.chapter.overview.model.ChapterRowData
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface ChapterScreenState {
    @Immutable
    data object Loading : ChapterScreenState

    @Immutable
    data class Ready(
        val title: String,
        val dominantColor: Color?,
        val isFavorite: Boolean,
        val chapters: ImmutableList<ChapterRowData>,
    ) : ChapterScreenState

    @Immutable
    data class Error(val message: String) : ChapterScreenState

    fun ifReady(): Ready? = this as? Ready
}
