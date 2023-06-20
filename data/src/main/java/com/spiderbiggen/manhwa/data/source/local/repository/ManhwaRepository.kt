package com.spiderbiggen.manhwa.data.source.local.repository

import com.spiderbiggen.manhwa.data.source.local.dao.LocalManhwaDao
import com.spiderbiggen.manhwa.data.source.local.model.LocalManhwaEntity
import com.spiderbiggen.manhwa.data.usecase.manhwa.mapper.ToDomainManhwaUseCase
import com.spiderbiggen.manhwa.domain.model.Manhwa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class ManhwaRepository @Inject constructor(
    private val manhwaDaoProvider: Provider<LocalManhwaDao>,
    private val toDomain: ToDomainManhwaUseCase,
) {

    private val manhwaDao
        get() = manhwaDaoProvider.get()

    fun getManhwas(): Result<Flow<List<Manhwa>>> = runCatching {
        manhwaDao.getAll().map { entities -> entities.map(toDomain::invoke) }
    }

    suspend fun getManhwa(id: String): Result<Manhwa?> = runCatching {
        manhwaDao.get(id)?.let(toDomain::invoke)
    }
}