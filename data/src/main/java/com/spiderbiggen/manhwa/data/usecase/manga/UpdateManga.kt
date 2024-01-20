package com.spiderbiggen.manhwa.data.usecase.manga

import com.spiderbiggen.manhwa.data.source.local.dao.LocalMangaDao
import com.spiderbiggen.manhwa.data.source.remote.usecase.GetRemoteMangaUseCase
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.data.usecase.manga.mapper.ToLocalMangaUseCase
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.mapLeft
import javax.inject.Inject
import javax.inject.Provider

class UpdateManga @Inject constructor(
    private val getRemoteManhwa: GetRemoteMangaUseCase,
    private val localManhwaDao: Provider<LocalMangaDao>,
    private val toLocal: ToLocalMangaUseCase,
) {
    suspend operator fun invoke(skipCache: Boolean): Either<Unit, AppError> =
        getRemoteManhwa(skipCache)
            .either()
            .mapLeft { manhwas ->
                localManhwaDao.get().insert(manhwas.map { toLocal(it) })
            }

}