package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.di.BaseUrl
import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapterImages
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class GetChapterImagesImpl @Inject constructor(
    @param:BaseUrl private val baseUrl: String,
    private val chapterRepository: ChapterRepository,
) : GetChapterImages {
    override suspend fun invoke(id: ChapterId): Either<ImmutableList<String>, AppError> =
        when (val either = chapterRepository.getChapterImages(id).either()) {
            is Either.Left -> {
                val chunks = either.value
                val images = (0 until chunks)
                    .map { index -> "$baseUrl/api/v1/chapters/${id.value}/images/$index" }
                    .toImmutableList()
                Either.Left(images)
            }

            is Either.Right -> Either.Right(either.value)
        }
}
