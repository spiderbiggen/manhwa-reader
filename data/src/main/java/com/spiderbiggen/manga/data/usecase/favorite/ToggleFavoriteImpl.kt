package com.spiderbiggen.manga.data.usecase.favorite

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote

class ToggleFavoriteImpl(
    private val favoritesRepository: FavoritesRepository,
    private val synchronizeWithRemote: SynchronizeWithRemote,
) : ToggleFavorite {
    override suspend fun invoke(id: MangaId): Either<AppError, Boolean> = either {
        val toggled = appError {
            val status = favoritesRepository.get(id).getOrThrow()
            val toggled = !status
            favoritesRepository.set(id, toggled).getOrThrow()
            toggled
        }
        synchronizeWithRemote(ignoreInterval = true)
        toggled
    }
}
