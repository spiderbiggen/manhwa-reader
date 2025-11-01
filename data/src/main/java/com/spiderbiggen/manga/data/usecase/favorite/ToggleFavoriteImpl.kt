package com.spiderbiggen.manga.data.usecase.favorite

import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import javax.inject.Inject

class ToggleFavoriteImpl @Inject constructor(private val favoritesRepository: FavoritesRepository) : ToggleFavorite {
    override suspend fun invoke(id: MangaId): Either<Boolean, AppError> = runCatching {
        val toggled = !favoritesRepository.get(id).getOrThrow()
        favoritesRepository.set(id, toggled).getOrThrow()
        toggled
    }.either()
}
