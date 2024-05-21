package com.spiderbiggen.manhwa.domain.usecase.read

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

fun interface ToggleRead {
    suspend operator fun invoke(chapterId: String): Either<Boolean, AppError>
}
