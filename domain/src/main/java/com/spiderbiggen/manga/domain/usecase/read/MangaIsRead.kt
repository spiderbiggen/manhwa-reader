package com.spiderbiggen.manga.domain.usecase.read

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId

fun interface MangaIsRead {
    suspend operator fun invoke(id: MangaId): Either<Boolean, AppError>
}
