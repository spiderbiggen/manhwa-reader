package com.spiderbiggen.manga.data.source.local.repository

import com.spiderbiggen.manga.data.source.local.dao.ChapterReadStatusDao
import com.spiderbiggen.manga.data.source.local.model.chapter.ChapterReadStatusEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.Flow

class ReadRepository @Inject constructor(private val readDaoProvider: Provider<ChapterReadStatusDao>) {

    fun getFlow(id: ChapterId): Result<Flow<Boolean?>> = runCatching {
        readDaoProvider.get().isReadFlow(id)
    }

    suspend fun get(id: ChapterId): Result<Boolean> = runCatching {
        readDaoProvider.get().isRead(id) == true
    }

    suspend fun set(id: ChapterId, isRead: Boolean): Result<Unit> = runCatching {
        readDaoProvider.get().insert(ChapterReadStatusEntity(id, isRead))
    }

    suspend fun set(ids: Set<ChapterId>, isRead: Boolean): Result<Unit> = runCatching {
        readDaoProvider.get().insert(ids.map { ChapterReadStatusEntity(it, isRead) })
    }
}
