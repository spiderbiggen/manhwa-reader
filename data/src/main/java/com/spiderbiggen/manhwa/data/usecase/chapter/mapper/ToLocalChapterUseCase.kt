package com.spiderbiggen.manhwa.data.usecase.chapter.mapper

import com.spiderbiggen.manhwa.data.source.local.model.LocalChapterEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ChapterEntity
import javax.inject.Inject

class ToLocalChapterUseCase @Inject constructor() {
    operator fun invoke(mangaId: String, entity: ChapterEntity) = LocalChapterEntity(
        id = entity.id,
        mangaId = mangaId,
        number = entity.number,
        title = entity.title,
        date = entity.date,
        updatedAt = entity.updatedAt,
        imageChunks = entity.images,
    )

    operator fun invoke(mangaId: String, entities: List<ChapterEntity>): List<LocalChapterEntity> =
        entities.map { invoke(mangaId, it) }
}
