package com.spiderbiggen.manga.domain.usecase.favorite

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.MangaId

fun interface ToggleFavorite {
    suspend operator fun invoke(id: MangaId): Either<AppError, Boolean>
}
