package com.spiderbiggen.manga.domain.usecase.manga

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.Manga

fun interface GetManga {
    suspend operator fun invoke(mangaId: String): Either<Manga, AppError>
}
