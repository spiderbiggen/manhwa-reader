package com.spiderbiggen.manga.data.source.remote.usecase

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.data.source.remote.model.MangaEntity
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import kotlin.time.Instant

class GetRemoteManga(private val mangaService: MangaService) {
    suspend operator fun invoke(
        since: Instant? = null,
        skipCache: Boolean = false,
    ): Either<AppError, List<MangaEntity>> = either {
        appError {
            mangaService.getAllMangas(since, skipCache = skipCache)
        }
    }
}
