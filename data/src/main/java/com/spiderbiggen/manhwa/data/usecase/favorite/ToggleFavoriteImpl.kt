package com.spiderbiggen.manhwa.data.usecase.favorite

import com.spiderbiggen.manhwa.data.source.remote.repository.FavoritesRepository
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.favorite.ToggleFavorite
import javax.inject.Inject

class ToggleFavoriteImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : ToggleFavorite {
    override suspend fun invoke(manhwaId: String): Either<Boolean, AppError> {
        val newState = !favoritesRepository.isFavorite(manhwaId)
        favoritesRepository.setFavorite(manhwaId, newState)
        return Either.Left(newState)
    }
}