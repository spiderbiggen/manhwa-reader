package com.spiderbiggen.manga.data.source.remote

import com.spiderbiggen.manga.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manga.data.source.remote.model.MangaEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant

interface MangaService {
    suspend fun getAllMangas(since: Instant?, skipCache: Boolean = false): List<MangaEntity>

    suspend fun getManga(id: MangaId): MangaEntity

    suspend fun getMangaChapters(id: MangaId, since: Instant?, skipCache: Boolean = false): List<ChapterEntity>

    suspend fun getChapter(id: ChapterId): ChapterEntity
}
