package com.spiderbiggen.manga.domain.usecase.favorite

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

fun interface HasFavorites {
    operator fun invoke(): Either<Boolean, AppError>
}
