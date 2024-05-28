package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import java.net.URL

fun interface GetChapterImages {
    suspend operator fun invoke(id: ChapterId): Either<List<URL>, AppError>
}
