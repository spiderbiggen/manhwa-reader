package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import com.spiderbiggen.manhwa.domain.model.Chapter

data class ChapterRowData(
    val chapter: Chapter,
    val isRead: Boolean,
)
