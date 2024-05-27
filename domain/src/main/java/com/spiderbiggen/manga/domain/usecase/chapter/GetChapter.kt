package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.domain.model.Either

fun interface GetChapter {
    suspend operator fun invoke(chapterId: String): Either<Chapter, AppError>
}
