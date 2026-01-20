package com.spiderbiggen.manga.domain.usecase.user

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError

fun interface SynchronizeWithRemote {
    suspend operator fun invoke(ignoreInterval: Boolean): Either<AppError, Unit>
}
