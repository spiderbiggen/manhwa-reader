package com.spiderbiggen.manhwa.data.usecase.manhwa.mapper

import com.spiderbiggen.manhwa.data.source.local.model.LocalManhwaEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ManhwaEntity
import com.spiderbiggen.manhwa.domain.model.Manhwa
import java.net.URL
import javax.inject.Inject

class ToDomainManhwaUseCase @Inject constructor() {
    operator fun invoke(entity: ManhwaEntity): Manhwa = Manhwa(
        source = entity.source,
        id = entity.id,
        title = entity.title,
        coverImage = URL(entity.coverImage),
        description = entity.description,
        status = entity.status,
        updatedAt = entity.updatedAt
    )

    operator fun invoke(entity: LocalManhwaEntity): Manhwa = Manhwa(
        source = entity.source,
        id = entity.id,
        title = entity.title,
        coverImage = URL(entity.coverImage),
        description = entity.description,
        status = entity.status,
        updatedAt = entity.updatedAt
    )
}