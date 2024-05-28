package com.spiderbiggen.manga.presentation.ui.chapter.model

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manga.domain.model.id.ChapterId

@Immutable
data class ChapterRowData(
    val id: ChapterId,
    val title: String,
    val date: String,
    val isRead: Boolean,
)
