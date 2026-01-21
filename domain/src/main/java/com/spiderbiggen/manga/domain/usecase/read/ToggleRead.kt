package com.spiderbiggen.manga.domain.usecase.read

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.ChapterId

fun interface ToggleRead {
    suspend operator fun invoke(id: ChapterId): Either<AppError, Boolean>
}
