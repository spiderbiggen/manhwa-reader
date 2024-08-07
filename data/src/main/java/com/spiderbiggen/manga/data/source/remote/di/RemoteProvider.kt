package com.spiderbiggen.manga.data.source.remote.di

import android.content.Context
import com.google.firebase.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.spiderbiggen.manga.data.di.BaseUrl
import com.spiderbiggen.manga.data.source.remote.MangaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object RemoteProvider {
    /**
     * Given the small size of the api responses,
     * 20MB should be enough for almost all manga + chapters.
     */
    private const val CACHE_SIZE: Long = 20 * 1024 * 1024

    @OptIn(ExperimentalSerializationApi::class)
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
    fun provideOkHttpClient(cache: Cache?): OkHttpClient = OkHttpClient.Builder()
        .cache(cache).apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
        }
        .build()

    @Provides
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient, json: Json): Retrofit.Builder {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
    }

    @Provides
    fun provideMangaService(builder: Retrofit.Builder, @BaseUrl baseUrl: String): MangaService =
        builder.baseUrl("$baseUrl/")
            .build()
            .create(MangaService::class.java)
}
