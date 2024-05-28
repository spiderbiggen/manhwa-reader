package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andThenLeft
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapter
import javax.inject.Inject

class GetChapterImpl @Inject constructor(
    private val chapterRepository: ChapterRepository,
) : GetChapter {
    override suspend fun invoke(id: ChapterId): Either<Chapter, AppError> =
        chapterRepository.getChapter(id).either()
            .andThenLeft { chapter ->
                chapter?.let { Either.Left(it) }
                    ?: Either.Right(AppError.Remote.NotFound)
            }
}
