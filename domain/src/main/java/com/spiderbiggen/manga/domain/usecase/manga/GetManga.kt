package com.spiderbiggen.manga.domain.usecase.manga

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.Manga
import com.spiderbiggen.manga.domain.model.id.MangaId

fun interface GetManga {
    suspend operator fun invoke(id: MangaId): Either<Manga, AppError>
}
