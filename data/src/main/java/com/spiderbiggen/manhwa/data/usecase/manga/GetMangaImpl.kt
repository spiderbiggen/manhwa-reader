package com.spiderbiggen.manhwa.data.usecase.manga

import com.spiderbiggen.manhwa.data.source.local.repository.MangaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manga
import com.spiderbiggen.manhwa.domain.model.andThenLeft
import com.spiderbiggen.manhwa.domain.usecase.manga.GetManga
import javax.inject.Inject

class GetMangaImpl @Inject constructor(
    private val mangaRepository: MangaRepository,
) : GetManga {
    override suspend fun invoke(mangaId: String): Either<Manga, AppError> =
        mangaRepository.getManga(mangaId)
            .either()
            .andThenLeft { result ->
                result?.let { Either.Left(it) }
                    ?: Either.Right(AppError.Remote.NotFound)
            }
}
