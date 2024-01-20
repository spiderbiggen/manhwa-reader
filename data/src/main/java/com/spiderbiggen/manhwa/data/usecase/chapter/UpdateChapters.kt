package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manhwa.data.source.remote.usecase.GetRemoteChaptersUseCase
import com.spiderbiggen.manhwa.data.usecase.chapter.mapper.ToLocalChapterUseCase
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.mapLeft
import javax.inject.Inject
import javax.inject.Provider

class UpdateChapters @Inject constructor(
    private val getRemoteChapters: GetRemoteChaptersUseCase,
    private val localChapterDao: Provider<LocalChapterDao>,
    private val toLocal: ToLocalChapterUseCase,
) {
    suspend operator fun invoke(mangaId: String, skipCache: Boolean): Either<Unit, AppError> =
        getRemoteChapters(mangaId, skipCache)
            .either()
            .mapLeft { chapters ->
                localChapterDao.get().insert(chapters.map { toLocal(mangaId, it) })
            }
}