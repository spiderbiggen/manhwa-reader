package com.spiderbiggen.manga.domain.usecase.auth

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.auth.User

fun interface Register {
    suspend operator fun invoke(username: String, email: String?, password: String): Either<AppError, User>
}
