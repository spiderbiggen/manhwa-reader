package com.spiderbiggen.manhwa.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.spiderbiggen.manhwa.data.source.remote.ManhwaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object DataProvider {

    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient, json: Json): Retrofit.Builder {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType))
    }

    @Provides
    fun provideManhwaService(builder: Retrofit.Builder): ManhwaService =
        builder.baseUrl("https://api.spiderbiggen.com/manhwa/")
            .build()
            .create(ManhwaService::class.java)
}