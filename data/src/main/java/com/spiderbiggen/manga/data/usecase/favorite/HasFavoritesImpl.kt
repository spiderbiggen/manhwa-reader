package com.spiderbiggen.manga.data.usecase.favorite

import com.spiderbiggen.manga.data.repository.FavoritesRepository
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.usecase.favorite.HasFavorites
import javax.inject.Inject

class HasFavoritesImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : HasFavorites {

    override fun invoke(): Either<Boolean, AppError> {
        return Either.Left(favoritesRepository.getFavorites().isNotEmpty())
    }
}
