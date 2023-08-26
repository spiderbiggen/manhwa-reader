package com.spiderbiggen.manhwa.data.usecase.read

import com.spiderbiggen.manhwa.data.repository.ReadRepository
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.read.IsRead
import javax.inject.Inject

class IsReadImpl @Inject constructor(
    private val readRepository: ReadRepository,
): IsRead {
    override suspend fun invoke(chapterId: String): Either<Boolean, AppError> =
        Either.Left(readRepository.isRead(chapterId))
}