package com.spiderbiggen.manhwa.domain.usecase.favorite

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

fun interface ToggleFavorite {
    operator fun invoke(mangaId: String): Either<Boolean, AppError>
}
