package com.spiderbiggen.manga.data.di

import android.content.Context
import androidx.room.Room
import com.spiderbiggen.manga.data.source.local.room.MangaDatabase
import com.spiderbiggen.manga.data.source.local.room.MangaDatabaseDecorator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { androidContext().getSharedPreferences("manga", Context.MODE_PRIVATE) }

    single {
        MangaDatabaseDecorator(
            Room.databaseBuilder(androidContext(), MangaDatabase::class.java, "manga")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build(),
        )
    }

    single { get<MangaDatabaseDecorator>().localMangaDao() }
    single { get<MangaDatabaseDecorator>().localChapterDao() }
    single { get<MangaDatabaseDecorator>().mangaFavoriteStatusDao() }
    single { get<MangaDatabaseDecorator>().chapterReadStatusDao() }
}
