package com.spiderbiggen.manhwa.data.usecase.chapter.mapper

import com.spiderbiggen.manhwa.data.source.local.model.LocalChapterEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ChapterEntity
import javax.inject.Inject

class ToLocalChapterUseCase @Inject constructor() {
    operator fun invoke(manhwaId: String, entity: ChapterEntity) = LocalChapterEntity(
        id = entity.id,
        manhwaId = manhwaId,
        number = entity.number,
        decimal = entity.decimal ?: 0,
        title = entity.title,
        date = entity.date,
        updatedAt = entity.updatedAt,
        imageChunks = entity.images,
    )
}