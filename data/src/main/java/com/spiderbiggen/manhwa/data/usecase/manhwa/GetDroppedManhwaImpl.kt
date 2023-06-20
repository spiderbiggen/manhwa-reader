package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.local.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetDroppedManhwa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDroppedManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
) : GetDroppedManhwa {
    override suspend fun invoke(): Either<Flow<List<Manhwa>>, AppError> =
        manhwaRepository.getManhwas()
            .either()
            .mapLeft { flow ->
                flow.map { manhwas -> manhwas.filter { it.status == "Dropped" } }
            }
}