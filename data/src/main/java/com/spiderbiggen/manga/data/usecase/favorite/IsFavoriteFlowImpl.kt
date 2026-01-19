package com.spiderbiggen.manga.data.usecase.favorite

import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavoriteFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IsFavoriteFlowImpl(private val favoritesRepository: FavoritesRepository) : IsFavoriteFlow {
    override fun invoke(id: MangaId): Flow<Boolean> = favoritesRepository.getFlow(id).map { it == true }
}
