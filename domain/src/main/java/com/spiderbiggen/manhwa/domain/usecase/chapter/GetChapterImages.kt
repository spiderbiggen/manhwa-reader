package com.spiderbiggen.manhwa.domain.usecase.chapter

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import java.net.URL

fun interface GetChapterImages {
    suspend operator fun invoke(chapterId: String): Either<List<URL>, AppError>
}
