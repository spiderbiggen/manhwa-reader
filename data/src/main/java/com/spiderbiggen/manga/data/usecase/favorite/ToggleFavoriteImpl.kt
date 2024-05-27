package com.spiderbiggen.manga.data.usecase.favorite

import com.spiderbiggen.manga.data.repository.FavoritesRepository
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import javax.inject.Inject

class ToggleFavoriteImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : ToggleFavorite {
    override fun invoke(mangaId: String): Either<Boolean, AppError> {
        val newState = !favoritesRepository.isFavorite(mangaId)
        favoritesRepository.setFavorite(mangaId, newState)
        return Either.Left(newState)
    }
}
