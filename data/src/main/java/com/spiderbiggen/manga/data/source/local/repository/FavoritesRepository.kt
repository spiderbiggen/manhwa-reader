package com.spiderbiggen.manga.data.source.local.repository

import com.spiderbiggen.manga.data.source.local.room.dao.MangaFavoriteStatusDao
import com.spiderbiggen.manga.data.source.local.room.model.manga.MangaFavoriteStatusEntity
import com.spiderbiggen.manga.data.source.remote.model.user.FavoriteState
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(private val favoritesDao: MangaFavoriteStatusDao) {
    fun getFlow(id: MangaId): Flow<Boolean?> = favoritesDao.isFavoriteFlow(id)

    suspend fun get(id: MangaId): Result<Boolean> = runCatching {
        favoritesDao.isFavorite(id) == true
    }

    suspend fun get(since: Instant): Result<List<MangaFavoriteStatusEntity>> = runCatching {
        favoritesDao.get(since)
    }

    suspend fun set(id: MangaId, isFavorite: Boolean): Result<Unit> = runCatching {
        favoritesDao.insert(MangaFavoriteStatusEntity(id, isFavorite))
    }

    suspend fun set(updates: Map<MangaId, FavoriteState>): Result<Unit> = runCatching {
        if (updates.isEmpty()) return@runCatching
        favoritesDao.insert(
            updates.map { (id, state) ->
                MangaFavoriteStatusEntity(id, state.isFavorite, state.updatedAt)
            },
        )
    }
}
