package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import java.net.URL

fun interface GetChapterImages {
    suspend operator fun invoke(chapterId: String): Either<List<URL>, AppError>
}
