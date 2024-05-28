package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId

fun interface GetSurroundingChapters {
    suspend operator fun invoke(id: ChapterId): Either<SurroundingChapters, AppError>
}
