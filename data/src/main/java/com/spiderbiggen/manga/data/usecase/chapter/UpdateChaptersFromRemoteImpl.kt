package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteChaptersUseCase
import com.spiderbiggen.manga.data.usecase.chapter.mapper.ToLocalChapterUseCase
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import arrow.core.Either
import arrow.core.flatMap
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote

class UpdateChaptersFromRemoteImpl(
    private val getRemoteChapters: GetRemoteChaptersUseCase,
    private val chapterRepository: ChapterRepository,
    private val toLocal: ToLocalChapterUseCase,
) : UpdateChaptersFromRemote {
    override suspend operator fun invoke(mangaId: MangaId, skipCache: Boolean): Either<AppError, Unit> {
        val since = chapterRepository.getLastUpdatedAtByMangaId(mangaId).getOrNull()
        return getRemoteChapters(mangaId, since, skipCache)
            .either()
            .flatMap { chapters ->
                chapterRepository.insert(toLocal(mangaId, chapters)).either()
            }
    }
}
