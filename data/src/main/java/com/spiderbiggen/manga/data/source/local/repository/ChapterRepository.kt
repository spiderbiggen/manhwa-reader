package com.spiderbiggen.manga.data.source.local.repository

import com.spiderbiggen.manga.data.source.local.room.dao.LocalChapterDao
import com.spiderbiggen.manga.data.source.local.room.model.chapter.LocalChapterEntity
import com.spiderbiggen.manga.data.usecase.chapter.mapper.ToDomainChapterUseCase
import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChapterRepository @Inject constructor(
    private val chapterDaoProvider: Provider<LocalChapterDao>,
    private val toDomain: ToDomainChapterUseCase,
) {
    private val chapterDao
        get() = chapterDaoProvider.get()

    suspend fun insert(chapters: List<LocalChapterEntity>) = runCatching {
        chapterDao.insert(chapters)
    }

    suspend fun getLastUpdatedAtByMangaId(mangaId: MangaId): Result<Instant?> = runCatching {
        chapterDao.getLastUpdatedAtByMangaId(mangaId)
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

    suspend fun getChapterImages(id: ChapterId): Result<Int> = runCatching {
        chapterDao.get(id)!!.imageChunks
    }

    suspend fun getPreviousChapters(id: ChapterId): Result<Set<ChapterId>> = runCatching {
        chapterDao.getPreviousChapterIds(id).toSet()
    }

    suspend fun getPreviousChapterId(id: ChapterId): Result<ChapterId?> = runCatching {
        chapterDao.getPrevChapterId(id)
    }

    suspend fun getNextChapterId(id: ChapterId): Result<ChapterId?> = runCatching {
        chapterDao.getNextChapterId(id)
    }
}
