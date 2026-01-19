package com.spiderbiggen.manga.data.di

import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.HttpClientFactory
import com.spiderbiggen.manga.data.source.remote.HttpLogger
import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.source.remote.impl.AuthServiceImpl
import com.spiderbiggen.manga.data.source.remote.impl.MangaServiceImpl
import com.spiderbiggen.manga.data.source.remote.impl.UserServiceImpl
import io.ktor.client.plugins.logging.Logger
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    single(named<BaseUrl>()) { "https://manga.spiderbiggen.com/" }

    factoryOf(::HttpLogger) bind Logger::class
    factory { HttpClientFactory(androidContext(), get(), get(), get()) }

    single {
        get<HttpClientFactory>().create(baseUrl = get(named<BaseUrl>()))
    }

    singleOf(::MangaServiceImpl) bind MangaService::class
    singleOf(::AuthServiceImpl) bind AuthService::class
    singleOf(::UserServiceImpl) bind UserService::class
}
