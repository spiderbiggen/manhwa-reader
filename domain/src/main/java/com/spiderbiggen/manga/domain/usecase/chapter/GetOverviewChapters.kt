package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.coroutines.flow.Flow

interface GetOverviewChapters {
    suspend operator fun invoke(id: MangaId): Either<Flow<List<ChapterForOverview>>, AppError>
}
