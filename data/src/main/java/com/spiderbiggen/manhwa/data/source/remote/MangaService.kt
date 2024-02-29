package com.spiderbiggen.manhwa.data.source.remote

import com.spiderbiggen.manhwa.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manhwa.data.source.remote.model.MangaEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface MangaService {
    @GET("mangas")
    suspend fun getAllMangas(): Response<List<MangaEntity>>

    @GET("mangas")
    @Headers("Cache-Control: max-age=60")
    suspend fun getAllMangasUncached(): Response<List<MangaEntity>>

    @GET("mangas/{id}")
    suspend fun getManga(@Path("id") id: String): Response<MangaEntity>

    @GET("mangas/{id}/chapters")
    suspend fun getMangaChapters(@Path("id") id: String): Response<List<ChapterEntity>>

    @GET("mangas/{id}/chapters")
    @Headers("Cache-Control: max-age=60")
    suspend fun getMangaChaptersUncached(@Path("id") id: String): Response<List<ChapterEntity>>

    @GET("chapters/{id}")
    suspend fun getChapter(@Path("id") id: String): Response<ChapterEntity>
}
