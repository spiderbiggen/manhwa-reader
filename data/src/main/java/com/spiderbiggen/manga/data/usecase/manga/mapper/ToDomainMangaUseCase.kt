package com.spiderbiggen.manga.data.usecase.manga.mapper

import com.spiderbiggen.manga.data.source.local.model.LocalMangaEntity
import com.spiderbiggen.manga.data.source.remote.model.MangaEntity
import com.spiderbiggen.manga.domain.model.Manga
import java.net.URL
import javax.inject.Inject

class ToDomainMangaUseCase @Inject constructor() {
    operator fun invoke(entity: MangaEntity): Manga = Manga(
        source = entity.source,
        id = entity.id,
        title = entity.title,
        coverImage = URL(entity.cover),
        dominantColor = entity.dominantColor,
        description = entity.description,
        status = entity.status,
        updatedAt = entity.updatedAt,
    )

    operator fun invoke(entity: LocalMangaEntity): Manga = Manga(
        source = entity.source,
        id = entity.id,
        title = entity.title,
        coverImage = URL(entity.cover),
        dominantColor = entity.dominantColor,
        description = entity.description,
        status = entity.status,
        updatedAt = entity.updatedAt,
    )
}
