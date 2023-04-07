package com.spiderbiggen.manhwa.data.source.remote

import com.spiderbiggen.manhwa.data.source.remote.model.ChapterWithImageChunksEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ManhwaChaptersResponseEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ManhwaEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ManhwaService {
    @GET("manhwas")
    suspend fun getAllManhwas(): Response<List<ManhwaEntity>>

    @GET("manhwas/{id}")
    suspend fun getManhwaChapters(@Path("id") id: String): Response<ManhwaChaptersResponseEntity>

    @GET("chapters/{id}")
    suspend fun getChapter(@Path("id") id: String): Response<ChapterWithImageChunksEntity>
}