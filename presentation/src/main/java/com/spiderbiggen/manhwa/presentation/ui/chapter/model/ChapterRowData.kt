package com.spiderbiggen.manhwa.presentation.ui.chapter.model

import androidx.compose.runtime.Immutable

@Immutable
data class ChapterRowData(
    val id: String,
    val title: String,
    val date: String,
    val isRead: Boolean,
)