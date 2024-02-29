package com.spiderbiggen.manhwa.domain.usecase.read

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

interface ToggleRead {
    suspend operator fun invoke(chapterId: String): Either<Boolean, AppError>
}
