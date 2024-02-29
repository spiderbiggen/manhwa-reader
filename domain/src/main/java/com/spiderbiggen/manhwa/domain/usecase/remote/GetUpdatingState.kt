package com.spiderbiggen.manhwa.domain.usecase.remote

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import kotlinx.coroutines.flow.Flow

interface GetUpdatingState {
    operator fun invoke(): Flow<Either<Boolean, AppError>>
}
