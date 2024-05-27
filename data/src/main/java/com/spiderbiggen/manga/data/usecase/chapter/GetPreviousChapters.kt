package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.domain.model.Either
import javax.inject.Inject

class GetPreviousChapters @Inject constructor(
    private val chapterRepository: ChapterRepository,
) {

    suspend operator fun invoke(chapterId: String): Either<List<Chapter>, AppError> =
        chapterRepository.getPreviousChapters(chapterId).either()
}
