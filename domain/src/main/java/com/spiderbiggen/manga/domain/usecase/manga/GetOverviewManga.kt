package com.spiderbiggen.manga.domain.usecase.manga

import com.spiderbiggen.manga.domain.model.manga.MangaForOverview
import kotlinx.coroutines.flow.Flow

fun interface GetOverviewManga {
    operator fun invoke(): Flow<List<MangaForOverview>>
}
