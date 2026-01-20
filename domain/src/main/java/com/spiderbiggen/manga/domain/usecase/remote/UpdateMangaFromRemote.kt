package com.spiderbiggen.manga.domain.usecase.remote

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError

fun interface UpdateMangaFromRemote {
    suspend operator fun invoke(skipCache: Boolean): Either<AppError, Unit>
}
