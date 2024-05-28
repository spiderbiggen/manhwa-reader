package com.spiderbiggen.manga.domain.usecase.remote

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

fun interface UpdateMangaFromRemote {
    suspend operator fun invoke(skipCache: Boolean): Either<Unit, AppError>
}
