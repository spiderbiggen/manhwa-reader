package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.SurroundingChapters
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetSurroundingChapters
import javax.inject.Inject

class GetSurroundingChaptersImpl @Inject constructor(
    private val getChapters: GetChapters
) : GetSurroundingChapters {
    override suspend fun invoke(
        manhwaId: String,
        chapterId: String
    ): Either<SurroundingChapters, AppError> = getChapters(manhwaId).mapLeft { chapters ->
        val index = chapters.indexOfFirst { it.id == chapterId }
        if (index < 0) {
            SurroundingChapters()
        } else {
            val previous = chapters.getOrNull(index + 1)
            val next = chapters.getOrNull(index - 1)
            SurroundingChapters(previous?.id, next?.id)
        }
    }
}