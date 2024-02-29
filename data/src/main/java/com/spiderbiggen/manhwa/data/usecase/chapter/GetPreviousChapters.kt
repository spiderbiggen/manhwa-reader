package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import javax.inject.Inject

class GetPreviousChapters @Inject constructor(
    private val chapterRepository: ChapterRepository,
) {

    suspend operator fun invoke(chapterId: String): Either<List<Chapter>, AppError> =
        chapterRepository.getPreviousChapters(chapterId).either()
}
