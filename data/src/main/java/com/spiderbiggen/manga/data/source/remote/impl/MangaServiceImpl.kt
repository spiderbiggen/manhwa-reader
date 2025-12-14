package com.spiderbiggen.manga.data.source.remote.impl

import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manga.data.source.remote.model.MangaEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import javax.inject.Inject
import kotlin.time.Instant

class MangaServiceImpl @Inject constructor(private val client: HttpClient) : MangaService {

    override suspend fun getAllMangas(since: Instant?, skipCache: Boolean): List<MangaEntity> =
        client.get("api/v1/mangas") {
            parameter("since", since)
            if (skipCache) header("Cache-Control", "max-age=15")
        }.body()

    override suspend fun getManga(id: MangaId): MangaEntity = client.get("api/v1/mangas/${id.value}").body()

    override suspend fun getMangaChapters(id: MangaId, since: Instant?, skipCache: Boolean): List<ChapterEntity> =
        client.get("api/v2/mangas/${id.value}/chapters") {
            parameter("since", since)
            if (skipCache) header("Cache-Control", "max-age=15")
        }.body()

    override suspend fun getChapter(id: ChapterId): ChapterEntity = client.get("api/v2/chapters/${id.value}").body()
}
