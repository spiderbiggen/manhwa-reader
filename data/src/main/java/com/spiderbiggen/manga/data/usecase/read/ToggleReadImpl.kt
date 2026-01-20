package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import arrow.core.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.read.ToggleRead
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote

class ToggleReadImpl(
    private val readRepository: ReadRepository,
    private val synchronizeWithRemote: SynchronizeWithRemote,
) : ToggleRead {
    override suspend fun invoke(id: ChapterId): Either<AppError, Boolean> = toggleReadStatus(id).either()
        .onRight { synchronizeWithRemote(ignoreInterval = true) }

    private suspend fun toggleReadStatus(id: ChapterId): Result<Boolean> = runCatching {
        val toggled = !readRepository.get(id).getOrThrow()
        readRepository.set(id, toggled).getOrThrow()
        toggled
    }
}
