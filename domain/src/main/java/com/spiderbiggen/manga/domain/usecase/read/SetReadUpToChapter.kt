package com.spiderbiggen.manga.domain.usecase.read

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

fun interface SetReadUpToChapter {
    suspend operator fun invoke(chapterId: String): Either<Unit, AppError>
}
