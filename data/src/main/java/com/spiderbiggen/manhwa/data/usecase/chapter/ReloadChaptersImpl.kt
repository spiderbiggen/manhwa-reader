package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.MapError
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.chapter.ReloadChapters
import javax.inject.Inject

class ReloadChaptersImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val mapError: MapError
) : ReloadChapters {
    override suspend fun invoke(manhwaId: String): Either<Unit, AppError> {
        return manhwaRepository.loadChapters(manhwaId).fold(
            { Either.Left(Unit) },
            { Either.Right(mapError(it)) },
        )
    }
}