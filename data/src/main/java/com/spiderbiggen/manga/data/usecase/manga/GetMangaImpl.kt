package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.Manga
import com.spiderbiggen.manga.domain.model.andThenLeft
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.manga.GetManga
import javax.inject.Inject

class GetMangaImpl @Inject constructor(
    private val mangaRepository: MangaRepository,
) : GetManga {
    override suspend fun invoke(id: MangaId): Either<Manga, AppError> = mangaRepository.getManga(id)
        .either()
        .andThenLeft { result ->
            result?.let { Either.Left(it) }
                ?: Either.Right(AppError.Remote.NotFound)
        }
}
