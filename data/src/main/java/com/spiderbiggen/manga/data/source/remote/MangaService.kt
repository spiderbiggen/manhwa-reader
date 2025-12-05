package com.spiderbiggen.manga.data.source.remote

import com.spiderbiggen.manga.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manga.data.source.remote.model.MangaEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface MangaService {
    @GET("api/v1/mangas")
    suspend fun getAllMangas(@Query("since") since: Instant?): Response<List<MangaEntity>>

    @GET("api/v1/mangas")
    @Headers("Cache-Control: max-age=15")
    suspend fun getAllMangasUncached(@Query("since") since: Instant?): Response<List<MangaEntity>>

    @GET("api/v1/mangas/{id}")
    suspend fun getManga(@Path("id") id: MangaId): Response<MangaEntity>

    @GET("api/v2/mangas/{id}/chapters")
    suspend fun getMangaChapters(
        @Path("id") id: MangaId,
        @Query("since") since: Instant?,
    ): Response<List<ChapterEntity>>

    @GET("api/v2/mangas/{id}/chapters")
    @Headers("Cache-Control: max-age=15")
    suspend fun getMangaChaptersSkipCache(
        @Path("id") id: MangaId,
        @Query("since") since: Instant?,
    ): Response<List<ChapterEntity>>

    @GET("api/v2/chapters/{id}")
    suspend fun getChapter(@Path("id") id: ChapterId): Response<ChapterEntity>
}
