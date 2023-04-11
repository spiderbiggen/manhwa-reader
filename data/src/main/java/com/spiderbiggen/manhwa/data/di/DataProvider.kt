package com.spiderbiggen.manhwa.data.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.spiderbiggen.manhwa.data.source.remote.ManhwaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @BaseUrl
    fun baseUrl(): String = "https://api.spiderbiggen.com/manhwa/"

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
            .client(okHttpClient)
    }

    @Provides
    fun provideManhwaService(builder: Retrofit.Builder, @BaseUrl baseUrl: String): ManhwaService =
        builder.baseUrl(baseUrl)
            .build()
            .create(ManhwaService::class.java)

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("manhwa", MODE_PRIVATE)
}