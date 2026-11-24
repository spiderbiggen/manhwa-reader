package com.spiderbiggen.manga.domain.usecase.update

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError

/** Looks for a newer release, at most once per throttle interval. */
fun interface CheckForUpdate {
    suspend operator fun invoke(): Either<AppError, Unit>
}
