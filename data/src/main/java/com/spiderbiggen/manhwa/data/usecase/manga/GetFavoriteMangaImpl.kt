package com.spiderbiggen.manhwa.data.usecase.manga

import com.spiderbiggen.manhwa.data.repository.FavoritesRepository
import com.spiderbiggen.manhwa.data.source.local.repository.MangaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manga
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.manga.GetFavoriteManga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteMangaImpl @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val favoritesRepository: FavoritesRepository,
) : GetFavoriteManga {

    override suspend fun invoke(): Either<Flow<List<Manga>>, AppError> {
        val favorites = favoritesRepository.getFavorites()
        return mangaRepository.getMangas()
            .either()
            .mapLeft { flow ->
                flow.map { mangas -> mangas.filter { it.id in favorites } }
            }
    }
}
