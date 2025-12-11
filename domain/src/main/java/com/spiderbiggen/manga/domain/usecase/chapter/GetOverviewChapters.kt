package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.coroutines.flow.Flow

fun interface GetOverviewChapters {
    operator fun invoke(id: MangaId): Flow<List<ChapterForOverview>>
}
