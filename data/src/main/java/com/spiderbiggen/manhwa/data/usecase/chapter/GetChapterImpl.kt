package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.MapError
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapter
import javax.inject.Inject

class GetChapterImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val mapError: MapError
) : GetChapter {
    override suspend fun invoke(manhwaId: String, chapterId: String): Either<Chapter, AppError> =
        manhwaRepository.getChapter(manhwaId, chapterId).fold(
            { result -> result?.let { Either.Left(it) } ?: Either.Right(AppError.Remote.NotFound) },
            { Either.Right(mapError(it)) }
        )
}