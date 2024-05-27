package com.spiderbiggen.manga.data.usecase.favorite

import com.spiderbiggen.manga.data.repository.FavoritesRepository
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavorite
import javax.inject.Inject

class IsFavoriteImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : IsFavorite {

    override fun invoke(mangaId: String): Either<Boolean, AppError> {
        return Either.Left(favoritesRepository.isFavorite(mangaId))
    }
}
