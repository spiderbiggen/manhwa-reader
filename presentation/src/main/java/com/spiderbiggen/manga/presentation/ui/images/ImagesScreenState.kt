package com.spiderbiggen.manga.presentation.ui.images

import com.spiderbiggen.manga.domain.model.SurroundingChapters

sealed interface ImagesScreenState {
    data object Loading : ImagesScreenState

    data class Ready(
        val title: String,
        val isFavorite: Boolean,
        val isRead: Boolean,
        val surrounding: SurroundingChapters,
        val images: List<String>,
    ) : ImagesScreenState

    data class Error(
        val errorMessage: String,
    ) : ImagesScreenState

    fun ifReady(): Ready? = this as? Ready
}
