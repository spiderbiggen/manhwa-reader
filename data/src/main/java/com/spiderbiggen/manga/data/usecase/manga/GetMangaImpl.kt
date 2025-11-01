package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.manga.MangaWithFavorite
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.manga.GetManga
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

class GetMangaImpl @Inject constructor(private val mangaRepository: MangaRepository) : GetManga {
    override suspend fun invoke(id: MangaId): Either<Flow<MangaWithFavorite>, AppError> =
        mangaRepository.getMangaWithFavoriteStatus(id).either()
            .mapLeft { it.filterNotNull() }
}
