package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
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
