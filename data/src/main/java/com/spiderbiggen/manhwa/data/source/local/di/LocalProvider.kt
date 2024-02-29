package com.spiderbiggen.manhwa.data.source.local.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.spiderbiggen.manhwa.data.source.local.MangaDatabase
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
object LocalProvider {
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("manhwa", MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MangaDatabase =
        Room.databaseBuilder(context, MangaDatabase::class.java, "manga")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMangaDao(database: MangaDatabase): LocalMangaDao = database.localMangaDao()

    @Provides
    fun provideChapterDao(database: MangaDatabase): LocalChapterDao = database.localChapterDao()
}
