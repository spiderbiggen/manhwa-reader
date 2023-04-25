package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.AppError.Remote.NotFound
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.andThenLeft
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manhwa.domain.usecase.chapter.ReloadChapters
import javax.inject.Inject

class GetChapterImpl @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val reloadChapters: ReloadChapters,
) : GetChapter {
    override suspend fun invoke(manhwaId: String, chapterId: String): Either<Chapter, AppError> =
        manhwaRepository.getChapter(manhwaId, chapterId).either().andThenLeft { result ->
            if (result?.images.isNullOrEmpty()) {
                reloadChapters.invoke(manhwaId)
                manhwaRepository.getChapter(manhwaId, chapterId).either().andThenLeft { updated ->
                    updated?.let { Either.Left(it) } ?: Either.Right(NotFound)
                }
            } else {
                Either.Left(result!!)
            }
        }
}