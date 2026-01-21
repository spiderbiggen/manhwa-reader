package com.spiderbiggen.manga.domain.usecase.auth

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError

fun interface Logout {
    suspend operator fun invoke(): Either<AppError, Unit>
}
