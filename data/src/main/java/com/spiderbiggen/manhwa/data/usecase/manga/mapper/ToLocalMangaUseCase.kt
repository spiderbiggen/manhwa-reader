package com.spiderbiggen.manhwa.data.usecase.manga.mapper

import com.spiderbiggen.manhwa.data.source.local.model.LocalMangaEntity
import com.spiderbiggen.manhwa.data.source.remote.model.MangaEntity
import javax.inject.Inject

class ToLocalMangaUseCase @Inject constructor() {
    operator fun invoke(entity: MangaEntity) = LocalMangaEntity(
        id = entity.id,
        source = entity.source,
        title = entity.title,
        cover = entity.cover,
        dominantColor = entity.dominantColor,
        description = entity.description,
        status = entity.status,
        updatedAt = entity.updatedAt,
    )
}
