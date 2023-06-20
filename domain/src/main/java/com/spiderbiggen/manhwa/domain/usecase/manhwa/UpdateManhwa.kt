package com.spiderbiggen.manhwa.domain.usecase.manhwa

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

interface UpdateManhwa {

    suspend operator fun invoke(): Either<Unit, AppError>
}