package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.chapter.GetOverviewChapters
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetOverviewChaptersImpl @Inject constructor(private val chapterRepository: ChapterRepository) :
    GetOverviewChapters {
    override suspend fun invoke(id: MangaId): Either<Flow<List<ChapterForOverview>>, AppError> =
        chapterRepository.getChaptersAsFlow(id).either()
}
