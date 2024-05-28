package com.spiderbiggen.manga.domain.usecase.remote

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId

fun interface UpdateChaptersFromRemote {
    suspend operator fun invoke(mangaId: MangaId, skipCache: Boolean): Either<Unit, AppError>
}
