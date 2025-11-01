package com.spiderbiggen.manga.presentation.ui.manga.model

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manga.domain.model.id.MangaId

@Immutable
data class MangaViewData(
    val id: MangaId,
    val source: String,
    val title: String,
    val status: String,
    val coverImage: String,
    val updatedAt: String?,
    val isFavorite: Boolean,
    val isRead: Boolean,
)
