package com.spiderbiggen.manhwa.data.usecase.chapter

import com.spiderbiggen.manhwa.data.di.BaseUrl
import com.spiderbiggen.manhwa.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapterImages
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
                    URL("$baseUrl/chapters/$chapterId/images/$index")
                }
                Either.Left(images)
            }

            is Either.Right -> Either.Right(either.right)
        }
    }
}
