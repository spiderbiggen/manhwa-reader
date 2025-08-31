package com.spiderbiggen.manga.presentation.ui.manga.overview

import com.spiderbiggen.manga.presentation.ui.manga.model.MangaViewData
import com.spiderbiggen.manga.presentation.ui.manga.overview.model.MangaState
import javax.inject.Inject
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MapMangaViewData @Inject constructor() {
    operator fun invoke(state: MangaState, timeZone: TimeZone) = MangaViewData(
        id = state.manga.id,
        source = state.manga.source,
        title = state.manga.title,
        status = state.manga.status,
        coverImage = state.manga.coverImage.toExternalForm(),
        updatedAt = state.manga.updatedAt.toLocalDateTime(timeZone).date.toString(),
        isFavorite = state.isFavorite,
        readAll = state.isRead,
    )
}
