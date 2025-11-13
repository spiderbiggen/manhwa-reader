package com.spiderbiggen.manga.data.source.remote.interceptors

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(private val repository: AuthenticationRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { repository.getAccessToken()?.token } ?: ""
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }
}
