package com.spiderbiggen.manhwa.data.usecase.manhwa

import com.spiderbiggen.manhwa.data.source.local.dao.LocalManhwaDao
import com.spiderbiggen.manhwa.data.source.remote.usecase.GetRemoteManhwaUseCase
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.data.usecase.manhwa.mapper.ToLocalManhwaUseCase
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.manhwa.UpdateManhwa
import javax.inject.Inject
import javax.inject.Provider

class UpdateManhwaImpl @Inject constructor(
    private val getRemoteManhwa: GetRemoteManhwaUseCase,
    private val localManhwaDao: Provider<LocalManhwaDao>,
    private val toLocal: ToLocalManhwaUseCase,
) : UpdateManhwa {
    override suspend fun invoke(): Either<Unit, AppError> =
        getRemoteManhwa()
            .either()
            .mapLeft { manhwas ->
                localManhwaDao.get().insert(manhwas.map { toLocal(it) })
            }

}