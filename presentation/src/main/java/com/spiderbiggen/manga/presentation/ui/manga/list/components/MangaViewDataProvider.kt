package com.spiderbiggen.manga.presentation.ui.manga.list.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaViewData

internal open class MangaViewDataProvider : PreviewParameterProvider<MangaViewData> {

    override val values: Sequence<MangaViewData>
        get() {
            return sequenceOf(
                MangaViewData(
                    source = "Asura",
                    id = MangaId("1"),
                    title = TITLE,
                    coverImage = COVER_IMAGE,
                    status = "Ongoing",
                    updatedAt = DATE_STRING,
                    isFavorite = false,
                    isRead = false,
                    dominantColor = WARM_DOMINANT_COLOR,
                ),
                MangaViewData(
                    source = "Asura",
                    id = MangaId("2"),
                    title = TITLE,
                    coverImage = COVER_IMAGE,
                    status = "Ongoing",
                    updatedAt = DATE_STRING,
                    isFavorite = true,
                    isRead = true,
                    dominantColor = COOL_DOMINANT_COLOR,
                ),
            )
        }

    private companion object {
        private const val DATE_STRING = "2023-04-23"
        private const val TITLE = "Heavenly Martial God"
        private const val COVER_IMAGE =
            "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg"
        private const val WARM_DOMINANT_COLOR = 0xFF8B4513.toInt()
        private const val COOL_DOMINANT_COLOR = 0xFF2F4F4F.toInt()
    }
}
