package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import javax.inject.Inject

class SetReadUpToChapterImpl @Inject constructor(private val readRepository: ReadRepository) : SetReadUpToChapter {
    override suspend fun invoke(id: ChapterId): Either<Unit, AppError> =
        readRepository.setReadForPreviousChapters(id).either()
}
