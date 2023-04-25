package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetDroppedManhwa
import javax.inject.Inject

class GetDroppedManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
) : GetDroppedManhwa {
    override suspend fun invoke(): Either<List<Manhwa>, AppError> =
        manhwaRepository.getManhwas().either()
            .mapLeft { manhwas -> manhwas.filter { it.status == "Dropped" } }
}