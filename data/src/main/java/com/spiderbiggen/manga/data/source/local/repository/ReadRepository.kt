package com.spiderbiggen.manga.data.source.local.repository

import com.spiderbiggen.manga.data.source.local.room.dao.ChapterReadStatusDao
import com.spiderbiggen.manga.data.source.local.room.model.chapter.ChapterReadStatusEntity
import com.spiderbiggen.manga.data.source.remote.model.user.ReadState
import com.spiderbiggen.manga.domain.model.id.ChapterId
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

class ReadRepository @Inject constructor(private val readDaoProvider: Provider<ChapterReadStatusDao>) {

    fun getFlow(id: ChapterId): Flow<Boolean?> = readDaoProvider.get().isReadFlow(id)

    suspend fun get(id: ChapterId): Result<Boolean> = runCatching {
        readDaoProvider.get().isRead(id) == true
    }

    suspend fun get(since: Instant): Result<List<ChapterReadStatusEntity>> = runCatching {
        readDaoProvider.get().get(since)
    }

    suspend fun set(id: ChapterId, isRead: Boolean): Result<Unit> = runCatching {
        readDaoProvider.get().insert(ChapterReadStatusEntity(id, isRead))
    }

    suspend fun set(ids: Set<ChapterId>, isRead: Boolean): Result<Unit> = runCatching {
        if (ids.isEmpty()) return@runCatching
        readDaoProvider.get().insert(ids.map { ChapterReadStatusEntity(it, isRead) })
    }

    suspend fun set(updates: Map<ChapterId, ReadState>): Result<Unit> = runCatching {
        if (updates.isEmpty()) return@runCatching
        readDaoProvider.get().insert(
            updates.map { (id, state) ->
                ChapterReadStatusEntity(id, state.isRead, state.updatedAt)
            },
        )
    }
}
