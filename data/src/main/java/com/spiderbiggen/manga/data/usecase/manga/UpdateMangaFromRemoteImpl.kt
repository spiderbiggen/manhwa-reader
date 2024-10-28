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
    private val getRemoteManga: GetRemoteMangaUseCase,
    private val localMangaDao: Provider<LocalMangaDao>,
    private val toLocal: ToLocalMangaUseCase,
) : UpdateMangaFromRemote {
    override suspend operator fun invoke(skipCache: Boolean): Either<Unit, AppError> = getRemoteManga(skipCache)
        .either()
        .mapLeft { mangas ->
            localMangaDao.get().insert(mangas.map(toLocal::invoke))
        }
}
