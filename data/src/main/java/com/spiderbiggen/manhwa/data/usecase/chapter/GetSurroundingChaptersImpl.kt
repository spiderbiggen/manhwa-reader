package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.SurroundingChapters
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetSurroundingChapters
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GetSurroundingChaptersImpl @Inject constructor(
    private val chapterRepository: ChapterRepository,
) : GetSurroundingChapters {
    override suspend fun invoke(chapterId: String): Either<SurroundingChapters, AppError> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val deferredPrev = async { chapterRepository.getPreviousChapter(chapterId) }
                val deferredNext = async { chapterRepository.getNextChapter(chapterId) }
                val prev = deferredPrev.await().getOrThrow()
                val next = deferredNext.await().getOrThrow()
                SurroundingChapters(prev?.id, next?.id)
            }.either()
        }
    }
}
