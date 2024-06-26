package com.spiderbiggen.manga.data.usecase.chapter.mapper

import com.spiderbiggen.manga.data.source.local.model.LocalChapterEntity
import com.spiderbiggen.manga.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manga.domain.model.Chapter
import javax.inject.Inject
import kotlinx.datetime.Instant

class ToDomainChapterUseCase @Inject constructor() {
    operator fun invoke(entity: ChapterEntity): Chapter = Chapter(
        id = entity.id,
        number = entity.number,
        title = entity.title,
        date = entity.date,
        updatedAt = entity.updatedAt,
    )

    operator fun invoke(entity: LocalChapterEntity): Chapter = Chapter(
        id = entity.id,
        number = entity.number,
        title = entity.title,
        date = entity.date,
        updatedAt = entity.updatedAt ?: Instant.DISTANT_PAST, // This should only happen once XD
    )
}
