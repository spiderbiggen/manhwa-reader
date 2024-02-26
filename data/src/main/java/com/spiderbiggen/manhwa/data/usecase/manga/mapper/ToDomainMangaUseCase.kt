package com.spiderbiggen.manhwa.data.usecase.manga.mapper

import com.spiderbiggen.manhwa.data.source.local.model.LocalMangaEntity
import com.spiderbiggen.manhwa.data.source.remote.model.MangaEntity
import com.spiderbiggen.manhwa.domain.model.Manga
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
        updatedAt = entity.updatedAt
    )

    operator fun invoke(entity: LocalMangaEntity): Manga = Manga(
        source = entity.source,
        id = entity.id,
        title = entity.title,
        coverImage = URL(entity.cover),
        dominantColor = entity.dominantColor,
        description = entity.description,
        status = entity.status,
        updatedAt = entity.updatedAt
    )
}