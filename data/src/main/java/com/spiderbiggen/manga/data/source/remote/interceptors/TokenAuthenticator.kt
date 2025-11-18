package com.spiderbiggen.manga.data.source.remote.interceptors

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

@Singleton
class TokenAuthenticator @Inject constructor(
    private val apiService: Provider<AuthService>,
    private val repository: Provider<AuthenticationRepository>,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent multiple refresh calls
        synchronized(this) {
            val tokens = runBlocking { repository.get().getAuthTokens() } ?: return null
            val currentAccessToken = tokens.accessToken
            val refreshToken = tokens.refreshToken

            // If the access token changed since the first failed request, retry with new token
            if (currentAccessToken.token != response.request.header("Authorization")?.removePrefix("Bearer ")) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            // Fetch new tokens synchronously
            val accessToken = runBlocking {
                val response = apiService.get().refresh(RefreshTokenBody(refreshToken.token))
                if (!response.isSuccessful) {
                    return@runBlocking null // Refresh failed, trigger logout
                }
                val body = response.body() ?: return@runBlocking null
                // Save new tokens
                repository.get().saveTokens(body.accessToken, body.refreshToken)
                body.accessToken
            } ?: return null

            // Retry the original request with new token
            return response.request.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        }
    }
}
