package com.spiderbiggen.manhwa.domain.usecase.chapter

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

interface UpdateChapters {

    suspend operator fun invoke(manhwaId: String): Either<Unit, AppError>
}