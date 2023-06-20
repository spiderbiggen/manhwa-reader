package com.spiderbiggen.manhwa.data.usecase.chapter.mapper

import com.spiderbiggen.manhwa.data.source.local.model.LocalChapterEntity
import com.spiderbiggen.manhwa.data.source.local.model.LocalManhwaEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ManhwaEntity
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Manhwa
import java.net.URL
import javax.inject.Inject

class ToDomainChapterUseCase @Inject constructor() {
    operator fun invoke(entity: ChapterEntity): Chapter = Chapter(
        id = entity.id,
        number = entity.number,
        decimal = entity.decimal,
        title =entity.title,
        date = entity.date,
        hasImages = entity.imageChunks != null,
    )

    operator fun invoke(entity: LocalChapterEntity): Chapter = Chapter(
        id = entity.id,
        number = entity.number,
        decimal = entity.decimal,
        title =entity.title,
        date = entity.date,
        hasImages = entity.imageChunks != null,
    )
}