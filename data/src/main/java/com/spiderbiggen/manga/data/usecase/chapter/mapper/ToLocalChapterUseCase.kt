package com.spiderbiggen.manga.data.usecase.chapter.mapper

import com.spiderbiggen.manga.data.source.local.model.LocalChapterEntity
import com.spiderbiggen.manga.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manga.domain.model.id.MangaId
import javax.inject.Inject

class ToLocalChapterUseCase @Inject constructor() {
    operator fun invoke(id: MangaId, entity: ChapterEntity) = LocalChapterEntity(
        id = entity.id,
        mangaId = id,
        number = entity.number,
        title = entity.title,
        date = entity.date,
        updatedAt = entity.updatedAt,
        imageChunks = entity.images,
    )

    operator fun invoke(id: MangaId, entities: List<ChapterEntity>): List<LocalChapterEntity> =
        entities.map { invoke(id, it) }
}
