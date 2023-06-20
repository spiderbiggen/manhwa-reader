package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manhwa.data.source.local.dao.LocalManhwaDao
import com.spiderbiggen.manhwa.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manhwa.data.source.remote.usecase.GetRemoteChaptersUseCase
import com.spiderbiggen.manhwa.data.source.remote.usecase.GetRemoteManhwaUseCase
import com.spiderbiggen.manhwa.data.usecase.chapter.mapper.ToLocalChapterUseCase
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.data.usecase.manhwa.mapper.ToLocalManhwaUseCase
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.chapter.UpdateChapters
import javax.inject.Inject
import javax.inject.Provider

class UpdateChaptersImpl @Inject constructor(
    private val getRemoteChapters: GetRemoteChaptersUseCase,
    private val localChapterDao: Provider<LocalChapterDao>,
    private val toLocal: ToLocalChapterUseCase,
) : UpdateChapters {
    override suspend fun invoke(manhwaId: String): Either<Unit, AppError> =
        getRemoteChapters(manhwaId)
            .either()
            .mapLeft { chapters ->
                localChapterDao.get().insert(chapters.map { toLocal(manhwaId, it) })
            }
}