package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.andThenLeft
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapter
import javax.inject.Inject

class GetChapterImpl @Inject constructor(
    private val chapterRepository: ChapterRepository,
) : GetChapter {
    override suspend fun invoke(chapterId: String): Either<Chapter, AppError> =
        chapterRepository.getChapter(chapterId).either()
            .andThenLeft { chapter ->
                chapter?.let { Either.Left(it) }
                    ?: Either.Right(AppError.Remote.NotFound)
            }
}
