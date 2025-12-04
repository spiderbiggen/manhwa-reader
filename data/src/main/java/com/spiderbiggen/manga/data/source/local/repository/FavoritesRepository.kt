package com.spiderbiggen.manga.data.source.local.repository

import com.spiderbiggen.manga.data.source.local.room.dao.MangaFavoriteStatusDao
import com.spiderbiggen.manga.data.source.local.room.model.manga.MangaFavoriteStatusEntity
import com.spiderbiggen.manga.domain.model.id.MangaId
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.Flow

class FavoritesRepository @Inject constructor(private val favoritesDaoProvider: Provider<MangaFavoriteStatusDao>) {
    fun getFlow(id: MangaId): Flow<Boolean?> = favoritesDaoProvider.get().isFavoriteFlow(id)

    suspend fun get(id: MangaId): Result<Boolean> = runCatching {
        favoritesDaoProvider.get().isFavorite(id) == true
    }

    suspend fun set(id: MangaId, isFavorite: Boolean): Result<Unit> = runCatching {
        favoritesDaoProvider.get().insert(MangaFavoriteStatusEntity(id, isFavorite))
    }
}
