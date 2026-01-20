package com.spiderbiggen.manga.data.usecase.read

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.read.ToggleRead
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote

class ToggleReadImpl(
    private val readRepository: ReadRepository,
    private val synchronizeWithRemote: SynchronizeWithRemote,
) : ToggleRead {
    override suspend fun invoke(id: ChapterId): Either<AppError, Boolean> = either {
        val toggled = appError {
            val status = readRepository.get(id).getOrThrow()
            val toggled = !status
            readRepository.set(id, toggled).getOrThrow()
            toggled
        }
        synchronizeWithRemote(ignoreInterval = true)
        toggled
    }
}
