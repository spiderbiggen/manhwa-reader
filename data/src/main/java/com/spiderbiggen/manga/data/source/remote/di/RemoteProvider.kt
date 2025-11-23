package com.spiderbiggen.manga.data.source.remote.di

import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.spiderbiggen.manga.data.BuildConfig
import com.spiderbiggen.manga.data.di.BaseUrl
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.data.source.remote.ProfileService
import com.spiderbiggen.manga.data.source.remote.interceptors.AuthorizationInterceptor
import com.spiderbiggen.manga.data.source.remote.interceptors.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

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
    fun baseUrl(): String = "https://manga.spiderbiggen.com"

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache = Cache(context.cacheDir, CACHE_SIZE)

    @Provides
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader = SingletonImageLoader.get(context)

    @Provides
    fun provideOkHttpClientBuilder(cache: Cache?): OkHttpClient.Builder = OkHttpClient.Builder().apply {
        cache(cache)
        if (BuildConfig.DEBUG) {
            addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
    }

    @Provides
    fun provideOkHttpClient(builder: OkHttpClient.Builder): OkHttpClient = builder.build()

    @Provides
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient, json: Json, @BaseUrl baseUrl: String): Retrofit.Builder {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
            .baseUrl("$baseUrl/")
    }

    @Provides
    fun provideMangaService(builder: Retrofit.Builder): MangaService = builder.build()
        .create(MangaService::class.java)

    @Provides
    fun provideAuthService(builder: Retrofit.Builder): AuthService = builder.build()
        .create(AuthService::class.java)

    @Provides
    fun provideProfileService(
        builder: Retrofit.Builder,
        okHttpBuilder: OkHttpClient.Builder,
        authInterceptor: AuthorizationInterceptor,
        authenticator: TokenAuthenticator,
    ): ProfileService {
        val okHttpClient = okHttpBuilder
            .addInterceptor(authInterceptor)
            .authenticator(authenticator)
            .build()
        return builder.client(okHttpClient)
            .build()
            .create(ProfileService::class.java)
    }
}
