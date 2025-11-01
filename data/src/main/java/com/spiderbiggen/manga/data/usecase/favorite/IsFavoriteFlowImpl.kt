package com.spiderbiggen.manga.data.usecase.favorite

import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavoriteFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IsFavoriteFlowImpl @Inject constructor(private val favoritesRepository: FavoritesRepository) : IsFavoriteFlow {

    override fun invoke(id: MangaId): Either<Flow<Boolean>, AppError> =
        favoritesRepository.getFlow(id).either().mapLeft { flow -> flow.map { it == true } }
}
