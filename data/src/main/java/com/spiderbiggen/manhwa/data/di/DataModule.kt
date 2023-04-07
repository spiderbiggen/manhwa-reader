package com.spiderbiggen.manhwa.data.di

import com.spiderbiggen.manhwa.data.source.remote.repository.ManhwaRepositoryImpl
import com.spiderbiggen.manhwa.domain.repository.ManhwaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindManhwaRepository(repository: ManhwaRepositoryImpl): ManhwaRepository
}