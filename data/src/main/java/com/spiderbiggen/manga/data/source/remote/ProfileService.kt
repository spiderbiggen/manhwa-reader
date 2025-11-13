package com.spiderbiggen.manga.data.source.remote

import com.spiderbiggen.manga.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manga.data.source.remote.model.MangaEntity
import com.spiderbiggen.manga.data.source.remote.model.UserEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ProfileService {

    @GET("api/v1/users/me")
    suspend fun getSelf(): Response<UserEntity>

}
