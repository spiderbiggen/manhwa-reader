package com.spiderbiggen.manga.data.di

import coil3.SingletonImageLoader
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    includes(networkModule, databaseModule, repositoryModule, useCaseModule)

    single { SingletonImageLoader.get(androidContext()) }
    factory { androidContext().contentResolver }
}
