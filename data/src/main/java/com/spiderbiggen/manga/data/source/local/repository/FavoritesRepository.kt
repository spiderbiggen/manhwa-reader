package com.spiderbiggen.manga.data.source.local.repository

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.room.dao.MangaFavoriteStatusDao
import com.spiderbiggen.manga.data.source.local.room.model.manga.MangaFavoriteStatusEntity
import com.spiderbiggen.manga.data.source.remote.model.user.FavoriteState
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(private val favoritesDao: MangaFavoriteStatusDao) {
    fun getFlow(id: MangaId): Flow<Boolean?> = favoritesDao.isFavoriteFlow(id)

    suspend fun get(id: MangaId): Either<AppError, Boolean> = either {
        appError { favoritesDao.isFavorite(id) == true }
    }

    suspend fun get(since: Instant): Either<AppError, List<MangaFavoriteStatusEntity>> = either {
        appError { favoritesDao.get(since) }
    }

    suspend fun set(id: MangaId, isFavorite: Boolean): Either<AppError, Unit> = either {
        appError { favoritesDao.insert(MangaFavoriteStatusEntity(id, isFavorite)) }
    }

    suspend fun set(updates: Map<MangaId, FavoriteState>): Either<AppError, Unit> = either {
        if (updates.isEmpty()) return@either
        appError {
            favoritesDao.insert(
                updates.map { (id, state) ->
                    MangaFavoriteStatusEntity(id, state.isFavorite, state.updatedAt)
                },
            )
        }
    }
}
