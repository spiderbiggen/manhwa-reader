package com.spiderbiggen.manga.data.usecase.chapter

import arrow.core.Either
import arrow.core.raise.either
import arrow.fx.coroutines.parZip
import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.chapter.GetSurroundingChapters

class GetSurroundingChaptersImpl(private val chapterRepository: ChapterRepository) : GetSurroundingChapters {
    override suspend fun invoke(id: ChapterId): Either<AppError, SurroundingChapters> = either {
        parZip(
            { chapterRepository.getPreviousChapterId(id).bind() },
            { chapterRepository.getNextChapterId(id).bind() },
        ) { prev, next ->
            SurroundingChapters(previous = prev, next = next)
        }
    }
}
