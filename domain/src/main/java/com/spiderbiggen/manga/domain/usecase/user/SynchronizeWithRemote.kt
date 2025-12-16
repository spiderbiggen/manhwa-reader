package com.spiderbiggen.manga.domain.usecase.user

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

fun interface SynchronizeWithRemote {
    suspend operator fun invoke(ignoreInterval: Boolean): Either<Unit, AppError>
}
