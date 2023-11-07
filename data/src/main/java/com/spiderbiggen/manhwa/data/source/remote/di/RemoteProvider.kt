package com.spiderbiggen.manhwa.data.source.remote.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.spiderbiggen.manhwa.data.di.BaseUrl
import com.spiderbiggen.manhwa.data.source.remote.ManhwaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteProvider {
    private const val CACHE_SIZE: Long = 20 * 1024 * 1024

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

    @Provides
    @BaseUrl
    fun baseUrl(): String =
        "https://api.spiderbiggen.com/manhwa"

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache? = null
//        Cache(context.cacheDir, CACHE_SIZE)


    @Provides
    fun provideOkHttpClient(cache: Cache?): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    @Provides
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient, json: Json): Retrofit.Builder {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
    }

    @Provides
    fun provideManhwaService(builder: Retrofit.Builder, @BaseUrl baseUrl: String): ManhwaService =
        builder.baseUrl("$baseUrl/")
            .build()
            .create(ManhwaService::class.java)
}