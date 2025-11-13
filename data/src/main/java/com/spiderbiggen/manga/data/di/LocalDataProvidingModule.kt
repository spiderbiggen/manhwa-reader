package com.spiderbiggen.manga.data.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.spiderbiggen.manga.data.source.local.room.MangaDatabase
import com.spiderbiggen.manga.data.source.local.room.MangaDatabaseDecorator
import com.spiderbiggen.manga.data.source.local.room.dao.ChapterReadStatusDao
import com.spiderbiggen.manga.data.source.local.room.dao.LocalChapterDao
import com.spiderbiggen.manga.data.source.local.room.dao.LocalMangaDao
import com.spiderbiggen.manga.data.source.local.room.dao.MangaFavoriteStatusDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataProvidingModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("manga", MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = MangaDatabaseDecorator(
        Room.databaseBuilder(context, MangaDatabase::class.java, "manga")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build(),
    )

    @Provides
    fun provideMangaDao(decorator: MangaDatabaseDecorator): LocalMangaDao = decorator.localMangaDao()

    @Provides
    fun provideChapterDao(decorator: MangaDatabaseDecorator): LocalChapterDao = decorator.localChapterDao()

    @Provides
    fun provideMangaFavoriteStatusDao(decorator: MangaDatabaseDecorator): MangaFavoriteStatusDao =
        decorator.mangaFavoriteStatusDao()

    @Provides
    fun provideChapterReadStatusDao(decorator: MangaDatabaseDecorator): ChapterReadStatusDao =
        decorator.chapterReadStatusDao()
}
