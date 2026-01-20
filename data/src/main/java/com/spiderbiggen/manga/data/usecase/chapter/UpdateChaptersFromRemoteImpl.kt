package com.spiderbiggen.manga.data.usecase.chapter

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteChaptersUseCase
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.data.usecase.chapter.mapper.ToLocalChapterUseCase
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote

class UpdateChaptersFromRemoteImpl(
    private val getRemoteChapters: GetRemoteChaptersUseCase,
    private val chapterRepository: ChapterRepository,
    private val toLocal: ToLocalChapterUseCase,
) : UpdateChaptersFromRemote {
    override suspend operator fun invoke(mangaId: MangaId, skipCache: Boolean): Either<AppError, Unit> = either {
        val since = chapterRepository.getLastUpdatedAtByMangaId(mangaId).getOrNull()
        val chapters = appError {
            getRemoteChapters(mangaId, since, skipCache).getOrThrow()
        }
        appError {
            chapterRepository.insert(toLocal(mangaId, chapters)).getOrThrow()
        }
    }
}
