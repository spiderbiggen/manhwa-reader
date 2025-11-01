package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import javax.inject.Inject

class SetReadUpToChapterImpl @Inject constructor(
    private val chapterRepository: ChapterRepository,
    private val readRepository: ReadRepository,
) : SetReadUpToChapter {
    override suspend fun invoke(id: ChapterId): Either<Unit, AppError> = runCatching {
        val ids = chapterRepository.getPreviousChapters(id).getOrThrow()
        readRepository.set(ids, true).getOrThrow()
    }.either()
}
