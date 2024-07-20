package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.data.usecase.chapter.GetPreviousChapters
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andThenLeft
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.read.SetRead
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import javax.inject.Inject

class SetReadUpToChapterImpl @Inject constructor(
    private val setRead: SetRead,
    private val getPreviousChapters: GetPreviousChapters,
) : SetReadUpToChapter {
    override suspend fun invoke(id: ChapterId): Either<Unit, AppError> = getPreviousChapters(id).andThenLeft { list ->
        setRead(list.map { it.id }, true)
    }
}
