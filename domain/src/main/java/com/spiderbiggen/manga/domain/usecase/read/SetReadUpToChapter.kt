package com.spiderbiggen.manga.domain.usecase.read

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId

fun interface SetReadUpToChapter {
    suspend operator fun invoke(id: ChapterId): Either<Unit, AppError>
}
