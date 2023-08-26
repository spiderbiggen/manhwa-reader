package com.spiderbiggen.manhwa.domain.usecase.read

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

interface SetRead {
    suspend operator fun invoke(chapterId: String, isRead: Boolean): Either<Unit, AppError>
}