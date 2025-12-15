package com.spiderbiggen.manga.data.source.remote.di

import android.content.Context
import android.util.Log
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.spiderbiggen.manga.data.BuildConfig
import com.spiderbiggen.manga.data.di.BaseUrl
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.source.remote.impl.AuthServiceImpl
import com.spiderbiggen.manga.data.source.remote.impl.MangaServiceImpl
import com.spiderbiggen.manga.data.source.remote.impl.UserServiceImpl
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.source.remote.model.auth.SessionResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
import javax.inject.Singleton
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object RemoteProvider {
    /**
     * Given the small size of the api responses,
     * 20MB should be enough for almost all manga + chapters.
     */
    private const val CACHE_SIZE: Long = 20 * 1024 * 1024

    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @BaseUrl
    fun baseUrl(): String = "https://manga.spiderbiggen.com/"

    @Provides
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader = SingletonImageLoader.get(context)

    @Singleton
    @Provides
    fun provideHttpClient(
        @ApplicationContext context: Context,
        @BaseUrl baseUrl: String,
        json: Json,
        authRepository: AuthenticationRepository,
    ): HttpClient = HttpClient(OkHttp) {
        val host = Url(baseUrl).host

        expectSuccess = true

        defaultRequest {
            url(baseUrl)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        install(ContentNegotiation) {
            json(json)
        }

        install(HttpCache) {
            val cacheFile = context.cacheDir.resolve("http-cache")
            publicStorage(FileStorage(cacheFile))
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 1_000
            socketTimeoutMillis = 5_000
        }

        install(ContentEncoding) {
            gzip(1.0F)
            deflate(0.9F)
        }

        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("HttpClient", message)
                    }
                }
                level = LogLevel.ALL
            }
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
                            sessionResponse.accessToken.token,
                            sessionResponse.refreshToken.token,
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

    @Provides
    fun provideMangaService(client: HttpClient): MangaService = MangaServiceImpl(client)

    @Provides
    fun provideAuthService(client: HttpClient): AuthService = AuthServiceImpl(client)

    @Provides
    fun provideProfileService(client: HttpClient): UserService = UserServiceImpl(client)
}
