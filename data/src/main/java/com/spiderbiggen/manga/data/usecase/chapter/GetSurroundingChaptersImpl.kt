package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.chapter.GetSurroundingChapters
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetSurroundingChaptersImpl @Inject constructor(
    private val chapterRepository: ChapterRepository,
) : GetSurroundingChapters {
    override suspend fun invoke(id: ChapterId): Either<SurroundingChapters, AppError> = runCatching {
        coroutineScope {
            val deferredPrev = async { chapterRepository.getPreviousChapter(id) }
            val deferredNext = async { chapterRepository.getNextChapter(id) }
            val prev = deferredPrev.await().getOrThrow()
            val next = deferredNext.await().getOrThrow()
            SurroundingChapters(prev?.id, next?.id)
        }
    }.either()
}
