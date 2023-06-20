package com.spiderbiggen.manhwa.data.usecase.manhwa.mapper

import com.spiderbiggen.manhwa.data.source.local.model.LocalManhwaEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ManhwaEntity
import javax.inject.Inject

class ToLocalManhwaUseCase @Inject constructor() {
    operator fun invoke(entity: ManhwaEntity) = LocalManhwaEntity(
        id = entity.id,
        source = entity.source,
        title = entity.title,
        coverImage = entity.coverImage,
        description = entity.description,
        status = entity.status,
        updatedAt = entity.updatedAt,
    )
}