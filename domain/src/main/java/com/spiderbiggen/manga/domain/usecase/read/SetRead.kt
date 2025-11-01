package com.spiderbiggen.manga.domain.usecase.read

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId

fun interface SetRead {
    suspend operator fun invoke(id: ChapterId, isRead: Boolean): Either<Unit, AppError>
}
