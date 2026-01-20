package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import arrow.core.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote

class SetReadUpToChapterImpl(
    private val chapterRepository: ChapterRepository,
    private val readRepository: ReadRepository,
    private val synchronizeWithRemote: SynchronizeWithRemote,
) : SetReadUpToChapter {
    override suspend fun invoke(id: ChapterId): Either<AppError, Unit> = markChaptersAsRead(id).either()
        .onRight { synchronizeWithRemote(ignoreInterval = true) }

    private suspend fun markChaptersAsRead(id: ChapterId) = runCatching {
        val ids = chapterRepository.getPreviousChapters(id).getOrThrow()
        readRepository.set(ids, true).getOrThrow()
    }
}
