package com.spiderbiggen.manhwa.domain.usecase.remote

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

fun interface UpdateMangaFromRemote {
    suspend operator fun invoke(skipCache: Boolean): Either<Unit, AppError>
}
