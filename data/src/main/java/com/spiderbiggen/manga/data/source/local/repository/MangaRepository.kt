package com.spiderbiggen.manga.data.source.local.repository

import com.spiderbiggen.manga.data.source.local.dao.LocalMangaDao
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToDomainMangaUseCase
import com.spiderbiggen.manga.domain.model.Manga
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MangaRepository @Inject constructor(
    private val mangaDaoProvider: Provider<LocalMangaDao>,
    private val toDomain: ToDomainMangaUseCase,
) {

    private val mangaDao
        get() = mangaDaoProvider.get()

    fun getMangas(): Result<Flow<List<Pair<Manga, String?>>>> = runCatching {
        mangaDao.getAll().map { entities ->
            entities.map { toDomain(it.manga) to it.lastChapterId }
                .distinctBy { it.first.id }
        }
    }

    suspend fun getManga(id: String): Result<Manga?> = runCatching {
        mangaDao.get(id)?.let(toDomain::invoke)
    }

    suspend fun getMangaForUpdate(): Result<Set<String>> = runCatching {
        mangaDao.getForUpdate().toSet()
    }
}
