package com.spiderbiggen.manga.data.source.local.repository

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.room.dao.ChapterReadStatusDao
import com.spiderbiggen.manga.data.source.local.room.model.chapter.ChapterReadStatusEntity
import com.spiderbiggen.manga.data.source.remote.model.user.ReadState
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

class ReadRepository(private val readDao: ChapterReadStatusDao) {

    fun getFlow(id: ChapterId): Flow<Boolean?> = readDao.isReadFlow(id)

    suspend fun get(id: ChapterId): Either<AppError, Boolean> = either {
        appError { readDao.isRead(id) == true }
    }

    suspend fun get(since: Instant): Either<AppError, List<ChapterReadStatusEntity>> = either {
        appError { readDao.get(since) }
    }

    suspend fun set(id: ChapterId, isRead: Boolean): Either<AppError, Unit> = either {
        appError { readDao.insert(ChapterReadStatusEntity(id, isRead)) }
    }

    suspend fun set(ids: Set<ChapterId>, isRead: Boolean): Either<AppError, Unit> = either {
        if (ids.isEmpty()) return@either
        appError { readDao.insert(ids.map { ChapterReadStatusEntity(it, isRead) }) }
    }

    suspend fun set(updates: Map<ChapterId, ReadState>): Either<AppError, Unit> = either {
        if (updates.isEmpty()) return@either
        appError {
            readDao.insert(
                updates.map { (id, state) ->
                    ChapterReadStatusEntity(id, state.isRead, state.updatedAt)
                },
            )
        }
    }
}
