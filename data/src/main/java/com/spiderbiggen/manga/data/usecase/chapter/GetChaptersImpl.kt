package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapters
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetChaptersImpl @Inject constructor(
    private val chapterRepository: ChapterRepository,
) : GetChapters {
    override suspend fun invoke(mangaId: String): Either<Flow<List<Chapter>>, AppError> =
        chapterRepository.getChapterFlow(mangaId).either()

    override suspend fun once(mangaId: String): Either<List<Chapter>, AppError> =
        chapterRepository.getChapters(mangaId).either()
}
