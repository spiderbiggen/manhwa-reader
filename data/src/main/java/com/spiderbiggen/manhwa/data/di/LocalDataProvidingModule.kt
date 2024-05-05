package com.spiderbiggen.manhwa.data.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.spiderbiggen.manhwa.data.source.local.MangaDatabase
import com.spiderbiggen.manhwa.data.source.local.MangaDatabaseDecorator
import com.spiderbiggen.manhwa.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manhwa.data.source.local.dao.LocalMangaDao
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
        context.getSharedPreferences("manhwa", MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = MangaDatabaseDecorator(
        Room.databaseBuilder(context, MangaDatabase::class.java, "manga")
            .fallbackToDestructiveMigration()
            .build(),
    )

    @Provides
    fun provideMangaDao(decorator: MangaDatabaseDecorator): LocalMangaDao = decorator.localMangaDao()

    @Provides
    fun provideChapterDao(decorator: MangaDatabaseDecorator): LocalChapterDao = decorator.localChapterDao()
}
