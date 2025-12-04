package com.spiderbiggen.manga.domain.usecase.favorite

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.coroutines.flow.Flow

fun interface IsFavoriteFlow {
    operator fun invoke(id: MangaId): Flow<Boolean>
}
