package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import java.net.URL

sealed interface ImagesScreenState {
    val title: String
    val previous: String?
    val next: String?
    val isFavorite: Boolean

    data class Loading(
        override val title: String,
        override val previous: String?,
        override val next: String?,
        override val isFavorite: Boolean
    ) : ImagesScreenState

    data class Ready(
        override val title: String,
        override val previous: String?,
        override val next: String?,
        override val isFavorite: Boolean,
        val images: List<URL>,
    ) : ImagesScreenState

    data class Error(
        override val title: String,
        override val previous: String?,
        override val next: String?,
        override val isFavorite: Boolean,
        val errorMessage: String
    ) : ImagesScreenState
}