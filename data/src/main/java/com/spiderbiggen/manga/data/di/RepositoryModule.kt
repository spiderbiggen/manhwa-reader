package com.spiderbiggen.manga.data.di

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AuthenticationRepository)
    singleOf(::FavoritesRepository)
    singleOf(::ReadRepository)
    singleOf(::MangaRepository)
    singleOf(::ChapterRepository)
}
