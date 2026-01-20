package com.spiderbiggen.manga.data.usecase.chapter

import android.util.Log
import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.chapter.GetSurroundingChapters
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetSurroundingChaptersImpl(private val chapterRepository: ChapterRepository) : GetSurroundingChapters {
    override suspend fun invoke(id: ChapterId): Either<AppError, SurroundingChapters> = either {
        appError {
            coroutineScope {
                val deferredPrev = async { chapterRepository.getPreviousChapterId(id) }
                val deferredNext = async { chapterRepository.getNextChapterId(id) }
                SurroundingChapters(
                    previous = deferredPrev.await().getOrThrow(),
                    next = deferredNext.await().getOrThrow(),
                )
            }
        }
    }
}
