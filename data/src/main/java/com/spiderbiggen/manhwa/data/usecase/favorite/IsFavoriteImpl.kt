package com.spiderbiggen.manhwa.data.usecase.favorite

import com.spiderbiggen.manhwa.data.repository.FavoritesRepository
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import javax.inject.Inject

class IsFavoriteImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : IsFavorite {

    override fun invoke(mangaId: String): Either<Boolean, AppError> {
        return Either.Left(favoritesRepository.isFavorite(mangaId))
    }
}
