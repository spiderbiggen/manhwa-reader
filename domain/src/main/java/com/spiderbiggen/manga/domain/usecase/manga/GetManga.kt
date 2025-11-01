package com.spiderbiggen.manga.domain.usecase.manga

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.manga.MangaWithFavorite
import kotlinx.coroutines.flow.Flow

fun interface GetManga {
    suspend operator fun invoke(id: MangaId): Either<Flow<MangaWithFavorite>, AppError>
}
