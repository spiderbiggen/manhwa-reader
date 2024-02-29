package com.spiderbiggen.manhwa.data.usecase.read

import com.spiderbiggen.manhwa.data.usecase.chapter.GetPreviousChapters
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.leftOrElse
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.read.SetRead
import com.spiderbiggen.manhwa.domain.usecase.read.SetReadUpToChapter
import javax.inject.Inject

class SetReadUpToChapterImpl @Inject constructor(
    private val setRead: SetRead,
    private val getPreviousChapters: GetPreviousChapters,
) : SetReadUpToChapter {
    override suspend fun invoke(chapterId: String): Either<Unit, AppError> =
        getPreviousChapters(chapterId).mapLeft { list ->
            list.forEach {
                setRead(it.id, true).leftOrElse { right -> return Either.Right(right) }
            }
        }
}
