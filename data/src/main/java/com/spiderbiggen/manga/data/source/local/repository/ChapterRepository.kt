package com.spiderbiggen.manga.data.source.local.repository

import com.spiderbiggen.manga.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manga.data.usecase.chapter.mapper.ToDomainChapterUseCase
import com.spiderbiggen.manga.domain.model.chapter.Chapter
import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ChapterRepository @Inject constructor(
    private val chapterDaoProvider: Provider<LocalChapterDao>,
    private val toDomain: ToDomainChapterUseCase,
) {
    private val chapterDao
        get() = chapterDaoProvider.get()

    fun getChaptersAsFlow(mangaId: MangaId): Result<Flow<List<ChapterForOverview>>> = runCatching {
        chapterDao.getFlowForMangaOverview(mangaId).map { entities ->
            entities.map {
                ChapterForOverview(
                    chapter = toDomain.invoke(it.chapter),
                    isRead = it.isRead,
                )
            }
        }
    }

    fun getChapterAsFlow(id: ChapterId): Result<Flow<ChapterForOverview?>> = runCatching {
        chapterDao.getFlowForChapterOverview(id).map {
            it?.let {
                ChapterForOverview(
                    chapter = toDomain.invoke(it.chapter),
                    isRead = it.isRead,
                )
            }
        }
    }

    suspend fun getChapter(id: ChapterId): Result<Chapter?> = runCatching {
        withContext(Dispatchers.IO) {
            chapterDao.get(id)!!.let(toDomain::invoke)
        }
    }

    suspend fun getChapterImages(id: ChapterId): Result<Int> = runCatching {
        withContext(Dispatchers.IO) {
            chapterDao.get(id)!!.imageChunks
        }
    }

    suspend fun getPreviousChapters(id: ChapterId): Result<List<Chapter>> = runCatching {
        withContext(Dispatchers.IO) {
            chapterDao.getPreviousChapters(id).map(toDomain::invoke)
        }
    }

    suspend fun getPreviousChapter(id: ChapterId): Result<Chapter?> = runCatching {
        withContext(Dispatchers.IO) {
            chapterDao.getPrevChapterId(id)?.let(toDomain::invoke)
        }
    }

    suspend fun getNextChapter(id: ChapterId): Result<Chapter?> = runCatching {
        withContext(Dispatchers.IO) {
            chapterDao.getNextChapterId(id)?.let(toDomain::invoke)
        }
    }
}
