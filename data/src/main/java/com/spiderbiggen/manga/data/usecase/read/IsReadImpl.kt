package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.read.IsReadFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IsReadImpl @Inject constructor(private val readRepository: ReadRepository) : IsReadFlow {
    override suspend fun invoke(id: ChapterId): Either<Flow<Boolean>, AppError> = readRepository.getFlow(id).either()
        .mapLeft { flow -> flow.map { it == true } }
}
