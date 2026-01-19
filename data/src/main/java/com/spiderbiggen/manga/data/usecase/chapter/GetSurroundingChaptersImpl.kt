package com.spiderbiggen.manga.data.usecase.chapter

import android.util.Log
import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.chapter.GetSurroundingChapters
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetSurroundingChaptersImpl(private val chapterRepository: ChapterRepository) :
    GetSurroundingChapters {
    override suspend fun invoke(id: ChapterId): Either<SurroundingChapters, AppError> = runCatching {
        coroutineScope {
            val deferredPrev = async { chapterRepository.getPreviousChapterId(id) }
            val deferredNext = async { chapterRepository.getNextChapterId(id) }
            SurroundingChapters(
                previous = deferredPrev.await().getOrThrow(),
                next = deferredNext.await().getOrThrow(),
            )
        }
    }
        .onFailure { Log.e("GetSurroundingChaptersImpl", "Failed to get surrounding chapters", it) }
        .either()
}
