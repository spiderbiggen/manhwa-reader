package com.spiderbiggen.manga.data.usecase.manga.mapper

import com.spiderbiggen.manga.data.source.local.room.model.manga.LocalMangaEntity
import com.spiderbiggen.manga.data.source.remote.model.MangaEntity
import com.spiderbiggen.manga.domain.model.manga.Manga

class ToDomainManga {
    operator fun invoke(entity: MangaEntity): Manga = Manga(
        source = entity.source,
        id = entity.id,
        title = entity.title,
        coverImage = entity.cover,
        dominantColor = entity.dominantColor,
        description = entity.description,
        status = entity.status,
        updatedAt = entity.updatedAt,
    )

    operator fun invoke(entity: LocalMangaEntity): Manga = Manga(
        source = entity.source,
        id = entity.id,
        title = entity.title,
        coverImage = entity.cover,
        dominantColor = entity.dominantColor,
        description = entity.description,
        status = entity.status,
        updatedAt = entity.updatedAt,
    )
}
