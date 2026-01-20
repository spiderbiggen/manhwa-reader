package com.spiderbiggen.manga.data.source.local.repository

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.room.dao.LocalChapterDao
import com.spiderbiggen.manga.data.source.local.room.model.chapter.LocalChapterEntity
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.data.usecase.chapter.mapper.ToDomainChapter
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChapterRepository(private val chapterDao: LocalChapterDao, private val toDomain: ToDomainChapter) {
    suspend fun insert(chapters: List<LocalChapterEntity>): Either<AppError, Unit> = either {
        appError { chapterDao.insert(chapters) }
    }

    suspend fun getLastUpdatedAtByMangaId(mangaId: MangaId): Either<AppError, Instant?> = either {
        appError { chapterDao.getLastUpdatedAtByMangaId(mangaId) }
    }

    fun getChaptersAsFlow(mangaId: MangaId): Flow<List<ChapterForOverview>> =
        chapterDao.getFlowForMangaOverview(mangaId)
            .map { entities ->
                entities.map {
                    ChapterForOverview(
                        chapter = toDomain.invoke(it.chapter),
                        isRead = it.isRead,
                    )
                }
            }

    fun getChapterAsFlow(id: ChapterId): Flow<ChapterForOverview?> = chapterDao.getFlowForChapterOverview(id)
        .map { entity ->
            entity?.let {
                ChapterForOverview(
                    chapter = toDomain.invoke(it.chapter),
                    isRead = it.isRead,
                )
            }
        }

    suspend fun getChapterImages(id: ChapterId): Either<AppError, Int> = either {
        appError { chapterDao.get(id)!!.imageChunks }
    }

    suspend fun getPreviousChapters(id: ChapterId): Either<AppError, Set<ChapterId>> = either {
        appError { chapterDao.getPreviousChapterIds(id).toSet() }
    }

    suspend fun getPreviousChapterId(id: ChapterId): Either<AppError, ChapterId?> = either {
        appError { chapterDao.getPrevChapterId(id) }
    }

    suspend fun getNextChapterId(id: ChapterId): Either<AppError, ChapterId?> = either {
        appError { chapterDao.getNextChapterId(id) }
    }
}
