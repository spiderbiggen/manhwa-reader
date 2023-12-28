package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChaptersImpl @Inject constructor(
    private val chapterRepository: ChapterRepository,
) : GetChapters {
    override suspend fun invoke(manhwaId: String): Either<Flow<List<Chapter>>, AppError> =
        chapterRepository.getChapterFlow(manhwaId).either()

    override suspend fun once(manhwaId: String): Either<List<Chapter>, AppError> =
        chapterRepository.getChapters(manhwaId).either()
}