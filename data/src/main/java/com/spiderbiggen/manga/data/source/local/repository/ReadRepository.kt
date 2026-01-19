package com.spiderbiggen.manga.data.source.local.repository

import com.spiderbiggen.manga.data.source.local.room.dao.ChapterReadStatusDao
import com.spiderbiggen.manga.data.source.local.room.model.chapter.ChapterReadStatusEntity
import com.spiderbiggen.manga.data.source.remote.model.user.ReadState
import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

class ReadRepository(private val readDao: ChapterReadStatusDao) {

    fun getFlow(id: ChapterId): Flow<Boolean?> = readDao.isReadFlow(id)

    suspend fun get(id: ChapterId): Result<Boolean> = runCatching {
        readDao.isRead(id) == true
    }

    suspend fun get(since: Instant): Result<List<ChapterReadStatusEntity>> = runCatching {
        readDao.get(since)
    }

    suspend fun set(id: ChapterId, isRead: Boolean): Result<Unit> = runCatching {
        readDao.insert(ChapterReadStatusEntity(id, isRead))
    }

    suspend fun set(ids: Set<ChapterId>, isRead: Boolean): Result<Unit> = runCatching {
        if (ids.isEmpty()) return@runCatching
        readDao.insert(ids.map { ChapterReadStatusEntity(it, isRead) })
    }

    suspend fun set(updates: Map<ChapterId, ReadState>): Result<Unit> = runCatching {
        if (updates.isEmpty()) return@runCatching
        readDao.insert(
            updates.map { (id, state) ->
                ChapterReadStatusEntity(id, state.isRead, state.updatedAt)
            },
        )
    }
}
