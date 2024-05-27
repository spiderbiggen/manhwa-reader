package com.spiderbiggen.manga.domain.usecase.favorite

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

fun interface IsFavorite {
    operator fun invoke(mangaId: String): Either<Boolean, AppError>
}
