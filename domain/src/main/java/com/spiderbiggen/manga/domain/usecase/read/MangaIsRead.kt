package com.spiderbiggen.manga.domain.usecase.read

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either

fun interface MangaIsRead {
    suspend operator fun invoke(mangaId: String): Either<Boolean, AppError>
}
