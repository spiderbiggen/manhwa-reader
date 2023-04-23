package com.spiderbiggen.manhwa.domain.usecase.favorite

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

interface IsFavorite {
    suspend operator fun invoke(manhwaId: String): Either<Boolean, AppError>
}