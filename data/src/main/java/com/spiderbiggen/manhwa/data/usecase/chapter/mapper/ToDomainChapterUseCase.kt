package com.spiderbiggen.manhwa.data.usecase.chapter.mapper

import com.spiderbiggen.manhwa.data.source.local.model.LocalChapterEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manhwa.domain.model.Chapter
import kotlinx.datetime.Instant
import javax.inject.Inject

class ToDomainChapterUseCase @Inject constructor() {
    operator fun invoke(entity: ChapterEntity): Chapter = Chapter(
        id = entity.id,
        number = entity.number,
        decimal = entity.decimal,
        title =entity.title,
        date = entity.date,
        updatedAt = entity.updatedAt,
    )

    operator fun invoke(entity: LocalChapterEntity): Chapter = Chapter(
        id = entity.id,
        number = entity.number,
        decimal = entity.decimal.takeIf { it > 0 },
        title =entity.title,
        date = entity.date,
        updatedAt = entity.updatedAt ?: Instant.DISTANT_PAST, // This should only happen once XD
    )
}