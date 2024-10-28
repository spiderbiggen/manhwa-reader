package com.spiderbiggen.manga.presentation.ui.chapter.overview.model

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manga.domain.model.id.ChapterId

@Immutable
data class ChapterRowData(
    val id: ChapterId,
    val number: String,
    val isHalfNumber: Boolean,
    val title: String?,
    val date: String,
    val isRead: Boolean,
)
