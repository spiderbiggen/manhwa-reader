package com.spiderbiggen.manga.domain.usecase.read

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId

interface SetRead {
    suspend operator fun invoke(id: ChapterId, isRead: Boolean): Either<Unit, AppError>
    suspend operator fun invoke(chapterIds: Iterable<ChapterId>, isRead: Boolean): Either<Unit, AppError>
}
