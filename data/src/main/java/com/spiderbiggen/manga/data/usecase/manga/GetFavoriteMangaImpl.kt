package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.repository.FavoritesRepository
import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.Manga
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.manga.GetFavoriteManga
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavoriteMangaImpl @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val favoritesRepository: FavoritesRepository,
) : GetFavoriteManga {

    override suspend fun invoke(): Either<Flow<List<Pair<Manga, String?>>>, AppError> {
        val favorites = favoritesRepository.getFavorites()
        return mangaRepository.getMangas()
            .either()
            .mapLeft { flow ->
                flow.map { it.filter { (manga, _) -> manga.id in favorites } }
            }
    }
}
