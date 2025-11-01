package com.spiderbiggen.manga.domain.usecase.read

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlinx.coroutines.flow.Flow

fun interface IsReadFlow {
    suspend operator fun invoke(id: ChapterId): Either<Flow<Boolean>, AppError>
}
