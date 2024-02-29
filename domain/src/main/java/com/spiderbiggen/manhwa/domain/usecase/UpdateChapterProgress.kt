package com.spiderbiggen.manhwa.domain.usecase

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either

interface UpdateChapterProgress {
    suspend operator fun invoke(chapterId: String, imageIndex: Int): Either<Unit, AppError>
}
