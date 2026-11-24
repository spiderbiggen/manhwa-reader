package com.spiderbiggen.manga.domain.usecase.update

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError

/** Hides the notice until the next check finds a release. */
fun interface DismissUpdateNotice {
    suspend operator fun invoke(): Either<AppError, Unit>
}
