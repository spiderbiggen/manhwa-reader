package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.MapError
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetActiveManhwa
import com.spiderbiggen.manhwa.domain.model.Either
import javax.inject.Inject

class GetActiveManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val mapError: MapError
) : GetActiveManhwa {
    override suspend fun invoke(): Either<List<Manhwa>, AppError> =
        manhwaRepository.getManhwa().fold(
            { Either.Left(it.filterNot { it.status == "Dropped" }) },
            { Either.Right(mapError(it)) },
        )
}