package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.local.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetActiveManhwa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetActiveManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
) : GetActiveManhwa {
    override suspend fun invoke(): Either<Flow<List<Manhwa>>, AppError> =
        manhwaRepository.getManhwas()
            .either()
            .mapLeft { flow ->
                flow.map { it.filterNot { manhwa -> manhwa.status == "Dropped" } }
            }
}