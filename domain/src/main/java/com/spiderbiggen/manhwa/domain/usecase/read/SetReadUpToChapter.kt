package com.spiderbiggen.manhwa.domain.usecase.read

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

interface SetReadUpToChapter {
    suspend operator fun invoke(chapterId: String): Either<Unit, AppError>
}