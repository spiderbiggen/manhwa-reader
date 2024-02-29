package com.spiderbiggen.manhwa.data.usecase.favorite

import com.spiderbiggen.manhwa.data.repository.FavoritesRepository
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.favorite.ToggleFavorite
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
