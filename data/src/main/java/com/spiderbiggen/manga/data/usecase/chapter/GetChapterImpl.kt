package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

class GetChapterImpl @Inject constructor(private val chapterRepository: ChapterRepository) : GetChapter {
    override suspend fun invoke(id: ChapterId): Either<Flow<ChapterForOverview>, AppError> =
        chapterRepository.getChapterAsFlow(id).either()
            .mapLeft { flow -> flow.filterNotNull() }
}
