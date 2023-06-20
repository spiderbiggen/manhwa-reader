package com.spiderbiggen.manhwa.domain.usecase.chapter

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import kotlinx.coroutines.flow.Flow
import java.net.URL

interface GetChapterImages {
    suspend operator fun invoke(chapterId: String): Either<List<URL>, AppError>
}