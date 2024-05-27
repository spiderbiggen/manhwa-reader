package com.spiderbiggen.manga.domain.usecase.manga

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.Manga
import kotlinx.coroutines.flow.Flow

fun interface GetFavoriteManga {
    suspend operator fun invoke(): Either<Flow<List<Pair<Manga, String?>>>, AppError>
}
