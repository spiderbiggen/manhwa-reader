package com.spiderbiggen.manga.data.source.remote.usecase

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant

class GetRemoteChapters(private val mangaService: MangaService) {
    suspend operator fun invoke(
        id: MangaId,
        since: Instant? = null,
        skipCache: Boolean = false,
    ): Either<AppError, List<ChapterEntity>> = either {
        appError {
            mangaService.getMangaChapters(id, since, skipCache)
        }
    }
}
