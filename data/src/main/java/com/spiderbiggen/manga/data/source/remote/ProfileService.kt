package com.spiderbiggen.manga.data.source.remote

import com.spiderbiggen.manga.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manga.data.source.remote.model.MangaEntity
import com.spiderbiggen.manga.data.source.remote.model.UserEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ProfileService {

    @GET("api/v1/users/me")
    suspend fun getSelf(): Response<UserEntity>

    @Multipart
    @POST("api/v1/users/profile/avatar")
    suspend fun updateImage(@Part avatar: MultipartBody.Part): Response<UserEntity>
}
