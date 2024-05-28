package com.spiderbiggen.manga.domain.usecase.favorite

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId

fun interface ToggleFavorite {
    operator fun invoke(id: MangaId): Either<Boolean, AppError>
}
