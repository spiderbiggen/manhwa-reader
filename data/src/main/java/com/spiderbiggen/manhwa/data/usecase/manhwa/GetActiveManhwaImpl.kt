package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetActiveManhwa
import javax.inject.Inject

class GetActiveManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
) : GetActiveManhwa {
    override suspend fun invoke(): Either<List<Manhwa>, AppError> =
        manhwaRepository.getManhwas().either()
            .mapLeft { manhwas -> manhwas.filterNot { it.status == "Dropped" } }
}