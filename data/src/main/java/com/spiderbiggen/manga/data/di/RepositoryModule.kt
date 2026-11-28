package com.spiderbiggen.manga.data.di

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationStore
import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.source.local.repository.UpdateRepository
import com.spiderbiggen.manga.data.source.local.repository.UpdateStore
import com.spiderbiggen.manga.data.source.local.repository.updateDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AuthenticationRepository) bind AuthenticationStore::class
    singleOf(::FavoritesRepository)
    singleOf(::ReadRepository)
    singleOf(::MangaRepository)
    singleOf(::ChapterRepository)
    single { androidContext().updateDataStore }
    singleOf(::UpdateRepository) bind UpdateStore::class
}
