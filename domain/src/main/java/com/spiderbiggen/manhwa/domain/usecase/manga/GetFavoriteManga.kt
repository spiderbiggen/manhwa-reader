package com.spiderbiggen.manhwa.domain.usecase.manga

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manga
import kotlinx.coroutines.flow.Flow

interface GetFavoriteManga {
    suspend operator fun invoke(): Either<Flow<List<Pair<Manga, String?>>>, AppError>
}
