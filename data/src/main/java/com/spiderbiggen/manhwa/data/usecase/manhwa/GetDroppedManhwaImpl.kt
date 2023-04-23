package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.MapError
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetDroppedManhwa
import com.spiderbiggen.manhwa.domain.model.Either
import javax.inject.Inject

class GetDroppedManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val mapError: MapError
) : GetDroppedManhwa {
    override suspend fun invoke(): Either<List<Manhwa>, AppError> =
        manhwaRepository.getManhwa().fold(
            { Either.Left(it.filter { it.status == "Dropped" }) },
            { Either.Right(mapError(it)) },
        )
}