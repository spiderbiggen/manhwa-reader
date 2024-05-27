package com.spiderbiggen.manga.domain.usecase.read

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

fun interface ToggleRead {
    suspend operator fun invoke(chapterId: String): Either<Boolean, AppError>
}
