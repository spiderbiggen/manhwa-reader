package com.spiderbiggen.manga.data.usecase.chapter

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteChapters
import com.spiderbiggen.manga.data.usecase.chapter.mapper.ToLocalChapter
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote

class UpdateChaptersFromRemoteImpl(
    private val getRemoteChapters: GetRemoteChapters,
    private val chapterRepository: ChapterRepository,
    private val toLocal: ToLocalChapter,
) : UpdateChaptersFromRemote {
    override suspend operator fun invoke(mangaId: MangaId, skipCache: Boolean): Either<AppError, Unit> = either {
        val since = chapterRepository.getLastUpdatedAtByMangaId(mangaId).getOrElse { null }
        val chapters = getRemoteChapters(mangaId, since, skipCache).bind()
        chapterRepository.insert(toLocal(mangaId, chapters)).bind()
    }
}
