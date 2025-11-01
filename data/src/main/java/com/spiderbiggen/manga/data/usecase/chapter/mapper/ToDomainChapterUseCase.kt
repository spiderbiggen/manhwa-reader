package com.spiderbiggen.manga.data.usecase.chapter.mapper

import com.spiderbiggen.manga.data.source.local.model.LocalChapterEntity
import com.spiderbiggen.manga.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manga.domain.model.Chapter
import javax.inject.Inject
import kotlin.time.Instant

class ToDomainChapterUseCase @Inject constructor() {
    operator fun invoke(entity: ChapterEntity): Chapter = Chapter(
        id = entity.id,
        index = entity.index,
        subIndex = entity.subIndex,
        title = entity.title,
        date = entity.date,
        updatedAt = entity.updatedAt,
    )

    operator fun invoke(entity: LocalChapterEntity): Chapter = Chapter(
        id = entity.id,
        index = entity.index.toUInt(),
        subIndex = entity.subIndex?.toUInt(),
        title = entity.title,
        date = entity.date,
        updatedAt = entity.updatedAt,
    )
}
