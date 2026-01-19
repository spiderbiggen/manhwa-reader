package com.spiderbiggen.manga.data.usecase.favorite

import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.onLeft
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote

class ToggleFavoriteImpl(
    private val favoritesRepository: FavoritesRepository,
    private val synchronizeWithRemote: SynchronizeWithRemote,
) : ToggleFavorite {
    override suspend fun invoke(id: MangaId): Either<Boolean, AppError> = toggleFavoriteStatus(id).either()
        .onLeft { synchronizeWithRemote(ignoreInterval = true) }

    private suspend fun toggleFavoriteStatus(id: MangaId): Result<Boolean> = runCatching {
        val toggled = !favoritesRepository.get(id).getOrThrow()
        favoritesRepository.set(id, toggled).getOrThrow()
        toggled
    }
}
