package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.dao.LocalMangaDao
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteMangaUseCase
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToLocalMangaUseCase
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote
import javax.inject.Inject
import javax.inject.Provider

class UpdateMangaFromRemoteImpl @Inject constructor(
    private val getRemotemanga: GetRemoteMangaUseCase,
    private val localmangaDao: Provider<LocalMangaDao>,
    private val toLocal: ToLocalMangaUseCase,
): UpdateMangaFromRemote {
    override suspend operator fun invoke(skipCache: Boolean): Either<Unit, AppError> =
        getRemotemanga(skipCache)
            .either()
            .mapLeft { mangas ->
                localmangaDao.get().insert(mangas.map(toLocal::invoke))
            }
}
