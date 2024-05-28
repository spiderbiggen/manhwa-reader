package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.repository.ReadRepository
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.read.ToggleRead
import javax.inject.Inject

class ToggleReadImpl @Inject constructor(
    private val repository: ReadRepository,
) : ToggleRead {
    override suspend fun invoke(id: ChapterId): Either<Boolean, AppError> {
        val toggled = !repository.isRead(id)
        repository.setRead(id, toggled)
        return Either.Left(toggled)
    }
}
