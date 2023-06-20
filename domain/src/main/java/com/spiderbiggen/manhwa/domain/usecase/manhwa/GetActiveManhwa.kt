package com.spiderbiggen.manhwa.domain.usecase.manhwa

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.Either
import kotlinx.coroutines.flow.Flow

interface GetActiveManhwa {
    suspend operator fun invoke(): Either<Flow<List<Manhwa>>, AppError>
}