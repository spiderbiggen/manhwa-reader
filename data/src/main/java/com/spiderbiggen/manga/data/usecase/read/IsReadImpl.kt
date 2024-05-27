package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.repository.ReadRepository
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.usecase.read.IsRead
import javax.inject.Inject

class IsReadImpl @Inject constructor(
    private val readRepository: ReadRepository,
) : IsRead {
    override fun invoke(chapterId: String): Either<Boolean, AppError> =
        Either.Left(readRepository.isRead(chapterId))
}
