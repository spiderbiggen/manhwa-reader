package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Manhwa

sealed interface ChapterScreenState {
    object Loading : ChapterScreenState
    data class Ready(val manhwa: Manhwa, val chapters: List<Chapter>) : ChapterScreenState
    data class Error(val message: String) : ChapterScreenState
}