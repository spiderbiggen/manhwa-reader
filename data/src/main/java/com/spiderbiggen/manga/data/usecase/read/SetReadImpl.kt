package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.read.SetRead
import javax.inject.Inject

class SetReadImpl @Inject constructor(private val readRepository: ReadRepository) : SetRead {
    override suspend fun invoke(id: ChapterId, isRead: Boolean): Either<Unit, AppError> =
        readRepository.set(id, isRead).either()
}
