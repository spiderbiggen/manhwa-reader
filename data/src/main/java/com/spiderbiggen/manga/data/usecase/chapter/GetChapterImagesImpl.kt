package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.di.BaseUrl
import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapterImages
import java.net.URL
import javax.inject.Inject

class GetChapterImagesImpl @Inject constructor(
    @BaseUrl private val baseUrl: String,
    private val chapterRepository: ChapterRepository,
) : GetChapterImages {
    override suspend fun invoke(chapterId: String): Either<List<URL>, AppError> {
        val either = chapterRepository.getChapterImages(chapterId).either()
        return when (either) {
            is Either.Left -> {
                val chunks = either.left
                val images = (0 until chunks).map { index ->
                    URL("$baseUrl/api/v1/chapters/$chapterId/images/$index")
                }
                Either.Left(images)
            }

            is Either.Right -> Either.Right(either.right)
        }
    }
}
