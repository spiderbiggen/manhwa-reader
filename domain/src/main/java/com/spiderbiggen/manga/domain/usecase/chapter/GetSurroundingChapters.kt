package com.spiderbiggen.manga.domain.usecase.chapter

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId

fun interface GetSurroundingChapters {
    suspend operator fun invoke(id: ChapterId): Either<AppError, SurroundingChapters>
}
