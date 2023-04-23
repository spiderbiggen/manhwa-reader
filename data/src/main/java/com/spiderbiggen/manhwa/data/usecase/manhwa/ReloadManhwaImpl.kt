package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.MapError
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.manhwa.ReloadManhwa
import javax.inject.Inject

class ReloadManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val mapError: MapError
) : ReloadManhwa {
    override suspend fun invoke(): Either<Unit, AppError> =
        manhwaRepository.loadManhwa().fold(
            { Either.Left(Unit) },
            { Either.Right(mapError(it)) },
        )
}