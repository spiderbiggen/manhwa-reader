package com.spiderbiggen.manga.domain.usecase.read

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

fun interface SetRead {
    suspend operator fun invoke(chapterId: String, isRead: Boolean): Either<Unit, AppError>
}
