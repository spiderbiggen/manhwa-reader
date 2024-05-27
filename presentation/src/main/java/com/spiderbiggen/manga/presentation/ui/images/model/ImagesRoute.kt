package com.spiderbiggen.manga.presentation.ui.images.model

import kotlinx.serialization.Serializable

@Serializable
data class ImagesRoute(val mangaId: String, val chapterId: String)
