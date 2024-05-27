package com.spiderbiggen.manga.domain.usecase.favorite

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

fun interface ToggleFavorite {
    operator fun invoke(mangaId: String): Either<Boolean, AppError>
}
