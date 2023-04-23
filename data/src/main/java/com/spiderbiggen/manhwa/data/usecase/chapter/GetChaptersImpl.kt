package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.MapError
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import javax.inject.Inject

class GetChaptersImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val mapError: MapError
) : GetChapters {
    override suspend fun invoke(manhwaId: String): Either<List<Chapter>, AppError> =
        manhwaRepository.getChapters(manhwaId).fold(
            { Either.Left(it) },
            { Either.Right(mapError(it)) }
        )
}