package com.spiderbiggen.manga.data.source.remote

import android.content.Context
import android.util.Log
import com.spiderbiggen.manga.data.BuildConfig
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.source.remote.model.auth.SessionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val context: Context,
    private val json: Json,
    private val authRepository: AuthenticationRepository,
    private val logger: Logger = Logger.DEFAULT
) {
    fun create(baseUrl: String): HttpClient {
        val host = Url(baseUrl).host

        return HttpClient(OkHttp) {
            expectSuccess = true

            defaultRequest {
                url(baseUrl)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            install(ContentNegotiation) {
                json(this@HttpClientFactory.json)
            }

            install(HttpCache) {
                val cacheFile = context.cacheDir.resolve("http-cache")
                publicStorage(FileStorage(cacheFile))
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 5_000
                socketTimeoutMillis = 5_000
            }

            install(ContentEncoding) {
                gzip(1.0F)
                deflate(0.9F)
            }

            install(Logging) {
                logger = this@HttpClientFactory.logger
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.INFO
                sanitizeHeader { it == HttpHeaders.Authorization }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        authRepository.getAuthenticatedState()?.let {
                            BearerTokens(it.accessToken.token, it.refreshToken.token)
                        }
                    }
                    refreshTokens {
                        val refreshToken = oldTokens?.refreshToken ?: return@refreshTokens null
                        val response = client.post("api/v1/auth/refresh") {
                            setBody(RefreshTokenBody(refreshToken))
                            markAsRefreshTokenRequest()
                        }
                        if (response.status == HttpStatusCode.OK) {
                            val sessionResponse: SessionResponse = response.body()
                            authRepository.saveTokens(
                                sessionResponse.accessToken,
                                sessionResponse.refreshToken,
                            )
                            BearerTokens(
                                accessToken = sessionResponse.accessToken.token,
                                refreshToken = sessionResponse.refreshToken.token,
                            )
                        } else {
                            null
                        }
                    }
                    sendWithoutRequest { request ->
                        request.url.host == host && !request.url.encodedPath.contains("/auth")
                    }
                }
            }
        }
    }
}
