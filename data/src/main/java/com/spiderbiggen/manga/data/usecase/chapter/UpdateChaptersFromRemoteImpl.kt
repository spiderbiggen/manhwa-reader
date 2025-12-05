package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.source.local.room.dao.LocalChapterDao
import com.spiderbiggen.manga.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteChaptersUseCase
import com.spiderbiggen.manga.data.usecase.chapter.mapper.ToLocalChapterUseCase
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andThenLeft
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote
import javax.inject.Inject
import javax.inject.Provider

class UpdateChaptersFromRemoteImpl @Inject constructor(
    private val getRemoteChapters: GetRemoteChaptersUseCase,
    private val chapterRepository: Provider<ChapterRepository>,
    private val toLocal: ToLocalChapterUseCase,
) : UpdateChaptersFromRemote {
    override suspend operator fun invoke(mangaId: MangaId, skipCache: Boolean): Either<Unit, AppError> {
        val chapterRepository = chapterRepository.get()
        val since = chapterRepository.getLastUpdatedAtByMangaId(mangaId).getOrNull()
        return getRemoteChapters(mangaId, since, skipCache)
            .either()
            .andThenLeft { chapters ->
                chapterRepository.insert(toLocal(mangaId, chapters)).either()
            }
    }
}
