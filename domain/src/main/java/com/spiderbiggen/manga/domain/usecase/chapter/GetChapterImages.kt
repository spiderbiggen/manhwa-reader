package com.spiderbiggen.manga.domain.usecase.chapter

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlinx.collections.immutable.ImmutableList

fun interface GetChapterImages {
    suspend operator fun invoke(id: ChapterId): Either<AppError, ImmutableList<String>>
}
