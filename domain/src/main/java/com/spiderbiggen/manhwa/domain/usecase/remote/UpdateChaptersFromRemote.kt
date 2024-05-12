package com.spiderbiggen.manhwa.domain.usecase.remote

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

fun interface UpdateChaptersFromRemote {
    suspend operator fun invoke(mangaId: String, skipCache: Boolean): Either<Unit, AppError>
}
