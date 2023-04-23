package com.spiderbiggen.manhwa.domain.usecase.chapter

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either

interface GetChapter {
    suspend operator fun invoke(manhwaId: String, chapterId: String): Either<Chapter, AppError>
}