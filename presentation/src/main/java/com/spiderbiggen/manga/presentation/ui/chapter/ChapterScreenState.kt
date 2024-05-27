package com.spiderbiggen.manga.presentation.ui.chapter

import androidx.compose.ui.graphics.Color
import com.spiderbiggen.manga.presentation.ui.chapter.model.ChapterRowData
import kotlinx.collections.immutable.ImmutableList

sealed interface ChapterScreenState {
    data object Loading : ChapterScreenState
    data class Ready(
        val title: String,
        val dominantColor: Color?,
        val isFavorite: Boolean,
        val chapters: ImmutableList<ChapterRowData>,
    ) : ChapterScreenState

    data class Error(val message: String) : ChapterScreenState

    fun ifReady(): Ready? = this as? Ready
}
