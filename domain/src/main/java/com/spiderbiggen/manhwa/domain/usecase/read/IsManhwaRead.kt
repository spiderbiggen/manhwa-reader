package com.spiderbiggen.manhwa.domain.usecase.read

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

fun interface IsManhwaRead {
    suspend operator fun invoke(manhwaId: String): Either<Boolean, AppError>
}