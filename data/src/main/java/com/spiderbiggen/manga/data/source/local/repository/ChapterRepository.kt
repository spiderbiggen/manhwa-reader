package com.spiderbiggen.manga.data.source.local.repository

import com.spiderbiggen.manga.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manga.data.usecase.chapter.mapper.ToDomainChapterUseCase
import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChapterRepository @Inject constructor(
    private val chapterDaoProvider: Provider<LocalChapterDao>,
    private val toDomain: ToDomainChapterUseCase,
) {
    private val chapterDao
        get() = chapterDaoProvider.get()

    fun getChapterFlow(mangaId: MangaId): Result<Flow<List<Chapter>>> = runCatching {
        chapterDao.getFlowForMangaId(mangaId).map { entities ->
            entities.map(toDomain::invoke)
        }
    }

    suspend fun getChapters(mangaId: MangaId): Result<List<Chapter>> = runCatching {
        chapterDao.getForMangaId(mangaId).map(toDomain::invoke)
    }

    suspend fun getChapter(id: ChapterId): Result<Chapter?> = runCatching {
        chapterDao.get(id)!!.let(toDomain::invoke)
    }

    suspend fun getChapterImages(id: ChapterId): Result<Int> = runCatching {
        chapterDao.get(id)!!.imageChunks
    }

    suspend fun getPreviousChapters(id: ChapterId): Result<List<Chapter>> = runCatching {
        chapterDao.getPreviousChapters(id).map(toDomain::invoke)
    }

    suspend fun getPreviousChapter(id: ChapterId): Result<Chapter?> = runCatching {
        chapterDao.getPrevChapterId(id)?.let(toDomain::invoke)
    }

    suspend fun getNextChapter(id: ChapterId): Result<Chapter?> = runCatching {
        chapterDao.getNextChapterId(id)?.let(toDomain::invoke)
    }
}
