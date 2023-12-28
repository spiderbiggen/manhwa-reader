package com.spiderbiggen.manhwa.data.source.local.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.spiderbiggen.manhwa.data.source.local.ManhwaDatabase
import com.spiderbiggen.manhwa.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manhwa.data.source.local.dao.LocalManhwaDao
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
    fun provideDatabase(@ApplicationContext context: Context): ManhwaDatabase =
        Room.databaseBuilder(context, ManhwaDatabase::class.java, "manhwa")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideManhwaDao(database: ManhwaDatabase): LocalManhwaDao = database.localManhwaDao()

    @Provides
    fun provideChapterDao(database: ManhwaDatabase): LocalChapterDao = database.localChapterDao()
}