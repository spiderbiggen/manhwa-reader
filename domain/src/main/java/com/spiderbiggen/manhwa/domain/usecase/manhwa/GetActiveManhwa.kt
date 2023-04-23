package com.spiderbiggen.manhwa.domain.usecase.manhwa

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.Either

interface GetActiveManhwa {
    suspend operator fun invoke(): Either<List<Manhwa>, AppError>
}