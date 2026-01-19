package com.spiderbiggen.manga.data.source.local.repository

import com.spiderbiggen.manga.data.source.local.room.dao.LocalMangaDao
import com.spiderbiggen.manga.data.source.local.room.model.manga.LocalMangaEntity
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToDomainMangaUseCase
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.manga.MangaForOverview
import com.spiderbiggen.manga.domain.model.manga.MangaWithFavorite
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MangaRepository(private val mangaDao: LocalMangaDao, private val toDomain: ToDomainMangaUseCase) {

    suspend fun insert(mangas: List<LocalMangaEntity>) = runCatching {
        mangaDao.insert(mangas)
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

    suspend fun getMangaForUpdate(): Result<Set<MangaId>> = runCatching {
        mangaDao.getForUpdate().toSet()
    }

    suspend fun getLastUpdatedAt(): Result<Instant?> = runCatching {
        mangaDao.getLastUpdatedAt()
    }
}
