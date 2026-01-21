package com.spiderbiggen.manga.data.source.local.repository

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.room.dao.LocalMangaDao
import com.spiderbiggen.manga.data.source.local.room.model.manga.LocalMangaEntity
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToDomainManga
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.manga.MangaForOverview
import com.spiderbiggen.manga.domain.model.manga.MangaWithFavorite
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MangaRepository(private val mangaDao: LocalMangaDao, private val toDomain: ToDomainManga) {

    suspend fun insert(mangas: List<LocalMangaEntity>): Either<AppError, Unit> = either {
        appError { mangaDao.insert(mangas) }
    }

    fun getMangasForOverview(): Flow<List<MangaForOverview>> = mangaDao.getAllNotDropped()
        .map { entities ->
            entities.map {
                MangaForOverview(
                    manga = toDomain(it.manga),
                    isFavorite = it.isFavorite,
                    isRead = it.isRead,
                    lastChapterId = it.lastChapterId,
                )
            }
        }

    fun getMangaWithFavoriteStatus(id: MangaId): Flow<MangaWithFavorite?> = mangaDao.getWithFavorite(id)
        .map { entity ->
            entity?.let {
                MangaWithFavorite(
                    manga = toDomain.invoke(it.manga),
                    isFavorite = it.isFavorite,
                )
            }
        }

    suspend fun getMangaForUpdate(): Either<AppError, Set<MangaId>> = either {
        appError { mangaDao.getForUpdate().toSet() }
    }

    suspend fun getLastUpdatedAt(): Either<AppError, Instant?> = either {
        appError { mangaDao.getLastUpdatedAt() }
    }
}
