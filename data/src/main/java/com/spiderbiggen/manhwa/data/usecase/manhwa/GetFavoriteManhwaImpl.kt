package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.remote.repository.FavoritesRepository
import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetFavoriteManhwa
import javax.inject.Inject

class GetFavoriteManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val favoritesRepository: FavoritesRepository,
) : GetFavoriteManhwa {

    override suspend fun invoke(): Either<List<Manhwa>, AppError> {
        val favorites = favoritesRepository.getFavorites()
        return manhwaRepository.getManhwas().either()
            .mapLeft { manhwas -> manhwas.filter { it.id in favorites } }
    }
}
