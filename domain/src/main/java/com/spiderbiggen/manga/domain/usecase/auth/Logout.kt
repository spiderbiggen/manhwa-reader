package com.spiderbiggen.manga.domain.usecase.auth

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

interface Logout {
    suspend operator fun invoke(): Either<Unit, AppError>
}
