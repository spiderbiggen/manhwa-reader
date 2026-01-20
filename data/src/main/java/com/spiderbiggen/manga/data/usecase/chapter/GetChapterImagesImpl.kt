package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import arrow.core.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapterImages
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class GetChapterImagesImpl(private val baseUrl: String, private val chapterRepository: ChapterRepository) :
    GetChapterImages {
    override suspend fun invoke(id: ChapterId): Either<AppError, ImmutableList<String>> =
        chapterRepository.getChapterImages(id)
            .either()
            .map { count ->
                (0 until count)
                    .map { index -> "$baseUrl/api/v1/chapters/${id.value}/images/$index" }
                    .toImmutableList()
            }
}
