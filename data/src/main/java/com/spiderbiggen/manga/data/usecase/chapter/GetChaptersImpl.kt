package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapters
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetChaptersImpl @Inject constructor(
    private val chapterRepository: ChapterRepository,
) : GetChapters {
    override suspend fun invoke(id: MangaId): Either<Flow<List<Chapter>>, AppError> =
        chapterRepository.getChapterFlow(id).either()

    override suspend fun once(id: MangaId): Either<List<Chapter>, AppError> =
        chapterRepository.getChapters(id).either()
}
