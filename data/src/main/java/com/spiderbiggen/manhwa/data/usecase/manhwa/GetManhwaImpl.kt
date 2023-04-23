package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.MapError
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetManhwa
import javax.inject.Inject

class GetManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val mapError: MapError
) : GetManhwa {
    override suspend fun invoke(manhwaId: String): Either<Manhwa, AppError> =
        manhwaRepository.getManhwa(manhwaId).fold(
            { result -> result?.let { Either.Left(it) } ?: Either.Right(AppError.Remote.NotFound) },
            { Either.Right(mapError(it)) }
        )
}