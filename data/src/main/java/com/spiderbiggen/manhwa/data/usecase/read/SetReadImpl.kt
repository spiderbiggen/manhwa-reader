package com.spiderbiggen.manhwa.data.usecase.read

import com.spiderbiggen.manhwa.data.repository.ReadRepository
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.read.SetRead
import javax.inject.Inject

class SetReadImpl @Inject constructor(
    private val readRepository: ReadRepository,
) : SetRead {
    override suspend fun invoke(chapterId: String, isRead: Boolean): Either<Unit, AppError> =
        Either.Left(readRepository.setRead(chapterId, isRead))
}