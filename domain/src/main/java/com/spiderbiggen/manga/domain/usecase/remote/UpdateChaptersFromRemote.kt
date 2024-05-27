package com.spiderbiggen.manga.domain.usecase.remote

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

fun interface UpdateChaptersFromRemote {
    suspend operator fun invoke(mangaId: String, skipCache: Boolean): Either<Unit, AppError>
}
