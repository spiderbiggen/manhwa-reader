package com.spiderbiggen.manga.presentation.ui.manga.list.model

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastCoerceIn
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
    val readChapterCount: Int = 0,
    val totalChapterCount: Int = 0,
    val dominantColor: Int?,
) {
    val readProgress: Float
        @FloatRange(0.0, 1.0)
        get() =
            if (totalChapterCount == 0) 0f
            else (readChapterCount.toFloat() / totalChapterCount).fastCoerceIn(0f, 1f)
}
