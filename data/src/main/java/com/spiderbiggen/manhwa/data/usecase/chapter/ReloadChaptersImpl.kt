package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.chapter.ReloadChapters
import javax.inject.Inject

class ReloadChaptersImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
) : ReloadChapters {
    override suspend fun invoke(manhwaId: String): Either<Unit, AppError> =
        manhwaRepository.loadChapters(manhwaId).either().mapLeft {}
}