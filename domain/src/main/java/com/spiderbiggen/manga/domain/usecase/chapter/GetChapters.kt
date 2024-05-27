package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.domain.model.Either
import kotlinx.coroutines.flow.Flow

interface GetChapters {
    suspend operator fun invoke(mangaId: String): Either<Flow<List<Chapter>>, AppError>
    suspend fun once(mangaId: String): Either<List<Chapter>, AppError>
}
