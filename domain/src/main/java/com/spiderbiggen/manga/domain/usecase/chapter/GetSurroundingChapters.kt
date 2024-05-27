package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.SurroundingChapters

fun interface GetSurroundingChapters {
    suspend operator fun invoke(chapterId: String): Either<SurroundingChapters, AppError>
}
