package com.spiderbiggen.manhwa.domain.usecase.chapter

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.SurroundingChapters

interface GetSurroundingChapters {
    suspend operator fun invoke(chapterId: String): Either<SurroundingChapters, AppError>
}