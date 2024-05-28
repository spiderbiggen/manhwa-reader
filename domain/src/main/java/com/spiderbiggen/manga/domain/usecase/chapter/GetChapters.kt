package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.coroutines.flow.Flow

interface GetChapters {
    suspend operator fun invoke(id: MangaId): Either<Flow<List<Chapter>>, AppError>
    suspend fun once(id: MangaId): Either<List<Chapter>, AppError>
}
