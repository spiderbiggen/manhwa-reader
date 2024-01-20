package com.spiderbiggen.manhwa.domain.usecase.read

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

fun interface MangaIsRead {
    suspend operator fun invoke(mangaId: String): Either<Boolean, AppError>
}