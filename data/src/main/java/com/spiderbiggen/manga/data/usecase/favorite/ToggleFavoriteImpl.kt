package com.spiderbiggen.manga.data.usecase.favorite

import com.spiderbiggen.manga.data.repository.FavoritesRepository
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import javax.inject.Inject

class ToggleFavoriteImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : ToggleFavorite {
    override fun invoke(id: MangaId): Either<Boolean, AppError> {
        val newState = !favoritesRepository.isFavorite(id)
        favoritesRepository.setFavorite(id, newState)
        return Either.Left(newState)
    }
}
