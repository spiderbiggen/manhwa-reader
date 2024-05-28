package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId

fun interface GetChapter {
    suspend operator fun invoke(id: ChapterId): Either<Chapter, AppError>
}
