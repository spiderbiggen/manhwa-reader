package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.remote.repository.FavoritesRepository
import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.MapError
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetFavoriteManhwa
import javax.inject.Inject

class GetFavoriteManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val favoritesRepository: FavoritesRepository,
    private val mapError: MapError
) : GetFavoriteManhwa {

    override suspend fun invoke(): Either<List<Manhwa>, AppError> {
        val favorites = favoritesRepository.getFavorites()
        return manhwaRepository.getManhwa().fold(
            { Either.Left(it.filter { it.id in favorites }) },
            { Either.Right(mapError(it)) },
        )
    }
}
