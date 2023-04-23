package com.spiderbiggen.manhwa.domain.usecase.manhwa

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

interface ReloadManhwa {

    suspend operator fun invoke(): Either<Unit, AppError>
}