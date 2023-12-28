package com.spiderbiggen.manhwa.domain.usecase.chapter

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import kotlinx.coroutines.flow.Flow

interface GetChapters {
    suspend operator fun invoke(manhwaId: String): Either<Flow<List<Chapter>>, AppError>
    suspend fun once(manhwaId: String): Either<List<Chapter>, AppError>
}