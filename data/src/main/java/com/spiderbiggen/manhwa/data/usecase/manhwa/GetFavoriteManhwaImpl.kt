package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.repository.FavoritesRepository
import com.spiderbiggen.manhwa.data.source.local.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetFavoriteManhwa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteManhwaImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val favoritesRepository: FavoritesRepository,
) : GetFavoriteManhwa {

    override suspend fun invoke(): Either<Flow<List<Manhwa>>, AppError> {
        val favorites = favoritesRepository.getFavorites()
        return manhwaRepository.getManhwas()
            .either()
            .mapLeft { flow ->
                flow.map { manhwas -> manhwas.filter { it.id in favorites } }
            }
    }
}
