package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.usecase.chapter.GetPreviousChapters
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.read.SetRead
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import javax.inject.Inject

class SetReadUpToChapterImpl @Inject constructor(
    private val setRead: SetRead,
    private val getPreviousChapters: GetPreviousChapters,
) : SetReadUpToChapter {
    override suspend fun invoke(id: ChapterId): Either<Unit, AppError> = getPreviousChapters(id).mapLeft { list ->
        list.forEach {
            setRead(it.id, true).leftOrElse { right -> return Either.Right(right) }
        }
    }
}
