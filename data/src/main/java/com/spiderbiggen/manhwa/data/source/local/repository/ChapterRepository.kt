package com.spiderbiggen.manhwa.data.source.local.repository

import com.spiderbiggen.manhwa.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manhwa.data.usecase.chapter.mapper.ToDomainChapterUseCase
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Manhwa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class ChapterRepository @Inject constructor(
    private val chapterDaoProvider: Provider<LocalChapterDao>,
    private val toDomain: ToDomainChapterUseCase,
) {
    private val chapterDao
        get() = chapterDaoProvider.get()

    fun getChapters(manhwaId: String): Result<Flow<List<Chapter>>> = runCatching {
        chapterDao.getForManhwaId(manhwaId).map { entities ->
            entities.map(toDomain::invoke)
        }
    }

    suspend fun getChapter(id: String): Result<Chapter?> = runCatching {
        chapterDao.get(id)?.let(toDomain::invoke)
    }

    suspend fun getChapterImages(id: String): Result<Int?> = runCatching {
        chapterDao.get(id)?.imageChunks
    }

    suspend fun getPreviousChapters(id: String): Result<List<Chapter>> = runCatching {
        chapterDao.getPreviousChapters(id).map(toDomain::invoke)
    }

    suspend fun getPreviousChapter(id: String): Result<Chapter?> = runCatching {
        chapterDao.getPrevChapterId(id)?.let(toDomain::invoke)
    }

    suspend fun getNextChapter(id: String): Result<Chapter?> = runCatching {
        chapterDao.getNextChapterId(id)?.let(toDomain::invoke)
    }
}