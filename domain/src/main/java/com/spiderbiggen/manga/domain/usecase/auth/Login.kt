package com.spiderbiggen.manga.domain.usecase.auth

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.auth.User

fun interface Login {
    suspend operator fun invoke(usernameOrEmail: String, password: String): Either<User, AppError>
}
