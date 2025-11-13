package com.spiderbiggen.manga.data.source.remote

import com.spiderbiggen.manga.data.source.remote.model.auth.LoginBody
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.source.remote.model.auth.RegisterBody
import com.spiderbiggen.manga.data.source.remote.model.auth.SessionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AuthService {
    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterBody): Response<SessionResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginBody): Response<SessionResponse>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenBody): Response<SessionResponse>

    @DELETE
    suspend fun logout(@Body body: RefreshTokenBody): Response<Unit>
}
