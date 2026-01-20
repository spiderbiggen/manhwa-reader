package com.spiderbiggen.manga.data.usecase.read

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.read.SetRead
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote

class SetReadImpl(
    private val readRepository: ReadRepository,
    private val synchronizeWithRemote: SynchronizeWithRemote,
) : SetRead {
    override suspend fun invoke(id: ChapterId, isRead: Boolean): Either<AppError, Unit> = either {
        appError {
            readRepository.set(id, isRead).getOrThrow()
        }
        synchronizeWithRemote(ignoreInterval = true)
    }
}
