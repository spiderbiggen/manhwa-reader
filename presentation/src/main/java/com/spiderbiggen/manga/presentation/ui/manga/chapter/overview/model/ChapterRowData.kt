package com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.model

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manga.domain.model.id.ChapterId

@Immutable
data class ChapterRowData(
    val id: ChapterId,
    val index: UInt,
    val subIndex: UInt?,
    val title: String?,
    val date: String,
    val isRead: Boolean,
)
