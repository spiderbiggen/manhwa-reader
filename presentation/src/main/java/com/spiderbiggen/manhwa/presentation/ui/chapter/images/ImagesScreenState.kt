package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import com.spiderbiggen.manhwa.domain.model.SurroundingChapters
import java.net.URL

sealed interface ImagesScreenState {
    object Loading : ImagesScreenState

    data class Ready(
        val title: String,
        val isFavorite: Boolean,
        val surrounding: SurroundingChapters,
        val images: List<String>,
    ) : ImagesScreenState

    data class Error(
        val errorMessage: String
    ) : ImagesScreenState
}