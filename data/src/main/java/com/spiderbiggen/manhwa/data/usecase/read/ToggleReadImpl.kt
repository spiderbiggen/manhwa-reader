package com.spiderbiggen.manhwa.data.usecase.read

import com.spiderbiggen.manhwa.data.repository.ReadRepository
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.read.ToggleRead
import javax.inject.Inject

class ToggleReadImpl @Inject constructor(
    private val repository: ReadRepository,
) : ToggleRead {
    override suspend fun invoke(chapterId: String): Either<Boolean, AppError> {
        val toggled = !repository.isRead(chapterId)
        repository.setRead(chapterId, toggled)
        return Either.Left(toggled)
    }
}
